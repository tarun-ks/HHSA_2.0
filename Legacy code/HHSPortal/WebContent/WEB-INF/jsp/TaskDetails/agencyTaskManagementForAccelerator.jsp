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
	src="${pageContext.servletContext.contextPath}/r2/js/agencyTaskManagementForAccelerator.js"></script>
<script type="text/javascript" src="../resources/js/calendar.js"></script>

<%-- 
This jsp is used for S265 and S266 - Agency Task Management
--%>

<portlet:defineObjects/>
<%-- Body Wrapper Start --%>
<%--resourceURL for typeheads--%>
<portlet:resourceURL var="fetchTypeAheadNameList" id="fetchTypeAheadNameList" escapeXml="false"/>
<%-- This portlet resource is for fetch user dynamically for financial tasks --%>
<portlet:resourceURL var="getReassignListFinance" id="getReassignListFinance"/>
<%-- This portlet ressource is for populate Assigned to filter --%>
<portlet:resourceURL var="getAssignedToFilter" id="getAssignedToFilter"/>

<%-- Enhancement 5688- Release 3.2.0 ||This portlet resource is for populate Program Name filter --%>
<portlet:resourceURL var="getProgramListForAccelerator" id="getProgramListForAccelerator"></portlet:resourceURL>
<input type="hidden" id="getProgramListForAccHidden" value="${getProgramListForAccelerator}"/>

<%-- This portlet action is for fetch  Task management mapping --%>
<portlet:actionURL var="agencyInboxAction" id="agencyInboxAction"/> 
	<input type = "hidden" value='${fetchTypeAheadNameList}' id="hiddenFetchTypeAheadNameList" />
	<form name="myTaskMform" id="myTaskMform" action="<portlet:actionURL/>" method ="post" >
	<input type="hidden" id="getReassignListFinanceHidden" value="${getReassignListFinance}" />
	<input type="hidden" id="getAssignedToFilterHidden" value="${getAssignedToFilter}" />
	<%-- Body Container Starts --%>
	<h2>Agency Tasks</h2>	
	
			<c:if test="${message ne null}">
				<div class="${messageType}" id="messagediv" style="display:block">${message} <img
					src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
					class="message-close" onclick="showMe('messagediv', this)">
				</div>
			</c:if>

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
				<input type="hidden" id="dropDownValuePrevassignedto" value="${loFilterToBeRetained.assignedTo}"/>
				<input type="hidden" id="dropDownValuePrevAgencyId" value="${loFilterToBeRetained.agencyId}"/>
				<input type="hidden" id="isFinanceTask" value="${loFilterToBeRetained.r3Task}"/>
				<input type="hidden" id="isR2TaskSelectAllDisable" value="${loFilterToBeRetained.r2TaskSelectAllDisable}"/>
				<%-- Popup for Filter Task Starts --%>
				<div id="documentValuePop" class='formcontainerFinance'>
					<div class='close'><a href="javascript:setVisibility('documentValuePop', 'none');" >X</a></div>
					
					<!-- Start || Added as a part of Enhancement #5688 for Release 3.2.0 -->
					<div class='row'>
						<span class='label'>Agency:</span>
						<span class='formfield'>
							<select id="agencySelectBox" name="agencySelectBox" class="widthFull">
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
					<!-- End || Added as a part of Enhancement #5688 for Release 3.2.0 -->
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
							<select disabled="disabled" class="widthFull" name="status" id="status">
							  </select>
						</span>
					</div>
					
					<!-- Start || Modified as a part of Enhancement #5688 for Release 3.2.0 -->
					<div class='row'>				       
				            <span class='label'>Program Name:</span>
				            <span class='formfield'>
				                  <select name="programname" class="widthFull" id="programname">				                  
								  </select>
				            </span>
				     </div>
					<!-- End || Modified as a part of Enhancement #5688 for Release 3.2.0 -->
				
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
							<input type="text" class="proposalConfigDrpdwn" id="providername" name="providername" onkeyup="setMaxLength(this,60)" onkeypress="setMaxLength(this,60)" value="${loFilterToBeRetained.providerName}"/>
						</span>
					</div>
				
					<div class='row'>
						<span class='label'>Date Submitted from:</span>
						<span class='formfield'>
							<span class='floatLft'>
								<input type="text" class='datepicker' name="datefrom" id="datefrom" validate="calender" maxlength="10" value="${loFilterToBeRetained.submittedFromDate}"/><img src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('datefrom',event,'mmddyyyy');return false;"/> 
							</span>
							<span class="error clear ieError7" id="datefromError"></span>
							<b>&nbsp;&nbsp;to:&nbsp;</b>
							<span>
									<input type="text" class='datepicker' name="dateto" id="dateto" validate="calender" maxlength="10" value="${loFilterToBeRetained.submittedToDate}"/><img src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('dateto',event,'mmddyyyy');return false;"/>
							</span>
							<span class="error clear ieError7" id="datetoError"></span>
						</span>
					</div>
					<div class='row'>
						<span class='label'>Assigned To</span>
						<span class='formfield'>
							<select name="assignedto" class="widthFull" id="assignedto" disabled>
								<option> </option>
									<c:forEach items="${userMap}" var="user">
       										 <option value="${user.key}">${user.value}</option>
    								</c:forEach>
							</select>
						</span>
					</div>
					<div class="buttonholder">
						<input type="button" class="graybtutton" value="Clear Filters" onclick="clearfilter();" />
						<input type="button" value="Filter" name="filter" id='filtersBtn' disabled="disabled" onclick='filtertask()'/>
					</div> 
				</div>
				<%-- Popup for Filter Task Ends --%>
				<c:choose>
					<c:when test="${filterchecked eq 'display:block'}">
						Tasks: ${TotalTask}
					</c:when>
					<c:otherwise>
						You must filter on a "Task Type" to view any tasks.
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<%--Filter and Reassign section ends --%>
		<c:if test="${TotalTask > 0}">
			<div class="" id='griddiv' style='${filterchecked}'>
			 <%-- Grid Starts --%>			 
			 <div class="tabularWrapper">
			 <st:table objectName="agencyTaskItemList" pageSize="${allowedObjectCount}" cssClass="heading"
				alternateCss1="evenRows" alternateCss2="oddRows" >
				<st:property headingName="Task Name"  columnName="taskName" sortType="taskName" sortValue="asc" align="center"
					size="22%" >		
					<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.AgencyTaskExtention" />
				</st:property>
				<st:property headingName="Procurement/Contract Title" columnName="procurementTitle" sortType="procurementTitle" sortValue="asc"
					align="left" size="20%"  />
					
					<%-- Start || Changes done for enahncement 6636 for Release 3.12.0 --%>	 
 				    <c:choose>
					<c:when test="${(loFilterToBeRetained.taskName eq 'Evaluate Proposal') or (loFilterToBeRetained.taskName eq 'Review Scores')}">  
 						<st:property  headingName="Proposal Id" columnName="proposalId" sortType="proposalId" sortValue="asc" align="right" size="9%" />
					</c:when>
				    </c:choose> 
			        <%-- End || Changes done for enahncement 6636 for Release 3.12.0 --%>

					
				<st:property headingName="Provider Name" columnName="providerName" sortType="providerName" sortValue="asc"
					align="left" size="15%"  />

			 <%-- [Starts: Add column only for Invoice Review -- R3.7.0 enhancement #6361 ] --%>			 
 				<c:choose>
					<c:when test="${loFilterToBeRetained.taskName eq 'Invoice Review'}">   
 						<st:property  headingName="Service Start Date" columnName="serviceStartDate" sortValue="asc" align="right" size="9%" />
						<st:property  headingName="Service End Date"   columnName="serviceEndDate"   align="right" size="9%" />
					</c:when>
				</c:choose> 
			 <%-- [Ends: Add column only for Invoice Review -- R3.7.0 enhancement #6361 ] --%>

				<st:property headingName="Date Submitted" columnName="dateCreated" sortType="dateCreated" sortValue="asc"
					align="right" size="15%" />
				<st:property headingName="Assigned To" columnName="assignedTo" sortType="assignedTo" sortValue="asc"
					align="right"  size="15%" />
				<st:property headingName="Status" columnName="status" sortType="status" sortValue="asc"
					align="right" size="10%" />
			</st:table>
			</div>
            <%-- Grid Ends --%>
			
			</div>
		</c:if>
		<input type="hidden" id="taskLevel" name="taskLevel"/>
</form>

<style type="text/css">
	.alert-box-contact {
		margin-left: 11%;
		top: 25%;
		width: 54%;
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