
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!--   BElpw styles added to remove status images from tabs -->
<script type="text/javascript">
	function formsubmit(form, str,tab) {
		document.myform.action = document.myform.action + '&usewindow=' + str+ '&taskTabValue=onTabClick&action='+tab;
		document.myform.submit();
	}

	$(document).ready(function() {

		// Accordion
		$("#accordion").accordion();

		// Tabs
		$('#tabs1').tabs();

		// Dialog
		$('#dialog').dialog({
			autoOpen : false,
			width : 650,
			buttons : {
				"Ok" : function() {
					$(this).dialog("close");
				},
				"Cancel" : function() {
					$(this).dialog("close");
				}
			}
		});

		// Dialog Link
		$('#dialog_link').click(function() {
			$('#dialog').dialog('open');
			return false;
		});

		// Slider
		$('#slider').slider({
			range : true,
			values : [ 17, 67 ]
		});

		// Progressbar
		$("#progressbar").progressbar({
			value : 20
		});

		//hover states on the static widgets
		$('#dialog_link, ul#icons li').hover(function() {
			$(this).addClass('ui-state-hover');
		}, function() {
			$(this).removeClass('ui-state-hover');
		});
		// Emergency build changes Start: 4.0.0.2 defect 8379
		$('#agencySelectBoxExport').on('change',function()
				{
			if($('#agencySelectBoxExport').val() != '')
				{
				// Emergency build changes End: 4.0.0.2 defect 8379
				$('#exportTask').removeAttr('disabled');
				}
			else
				{
				$('#exportTask').attr('disabled','disabled');
				}
				});

	});
</script>
<portlet:defineObjects />
<%
	String lsFilePath = "";
	String lsClassNameForTab1;
	String lsClassNameForTab2;
	String lsClassNameForTab3;
	String classSe = "class='selected'";
	if (renderRequest.getAttribute("IncludeInbox") != null) {
		lsFilePath = (String) renderRequest.getAttribute("IncludeInbox");
		lsClassNameForTab1 = "class='selected'";
	} else {
		lsClassNameForTab1 = "";
	}
	if (renderRequest.getAttribute("IncludeManagementBox") != null) {
		lsFilePath = (String) renderRequest.getAttribute("IncludeManagementBox");
		lsClassNameForTab2 = "class='selected'";
	} else {
		lsClassNameForTab2 = "";
	}
	if (renderRequest.getAttribute("IncludeAgencyTaskManagementBox") != null) {
		lsFilePath = (String) renderRequest.getAttribute("IncludeAgencyTaskManagementBox");
		lsClassNameForTab3 = "class='selected'";
	} else {
		lsClassNameForTab3 = "";
	}
%>

<%-- Start changes for R5 --%>
<portlet:resourceURL var="exportAllTask" id="exportAllTask" escapeXml="false">
</portlet:resourceURL>
<input type = 'hidden' value='${exportAllTask}' id='exportAllTask'/> 
<div id="transactionStatusDiv" class="passed breakAll" style="display:none" ></div>
<%-- End changes for R5 --%>
<h2>Tasks</h2>
<div class="clear"></div>
<form action="<portlet:actionURL/>" method="post" name="myform">
<%-- Start changes for R5 --%>
<input type="hidden" name="contextPathVal" id="contextPathVal" value='${pageContext.servletContext.contextPath}' />
	<div class="customtabs">
		<ul class='normalCustomTab'>
			<li <%=lsClassNameForTab1%>><a class="<%=lsClassNameForTab1%>" href="<portlet:actionURL><portlet:param name='controller_action' value='inboxController' /><portlet:param name='usewindow' value='inbox' /><portlet:param name='taskTabValue' value='onTabClick' /></portlet:actionURL>">Task Inbox</a></li>
			<li <%=lsClassNameForTab2%>><a class="<%=lsClassNameForTab2%>" href="<portlet:actionURL><portlet:param name='controller_action' value='inboxController' /><portlet:param name='usewindow' value='taskmanager' /><portlet:param name='taskTabValue' value='onTabClick' /></portlet:actionURL>">Task Management</a></li>
			<li <%=lsClassNameForTab3%>><a class="<%=lsClassNameForTab3%>" href="<portlet:renderURL><portlet:param name='controller_action' value='agencyWorkflowCity' /><portlet:param name='usewindow' value='agencyTaskForCity' /><portlet:param name='taskTabValue' value='onTabClick' /></portlet:renderURL>">Agency Tasks</a></li>	
		<div class="exporttasklink"><a href='#' title='Export all the current task information for your organization or another agency' onclick='exportTaskListPopUp();'>Export Task</a></div>
		</ul>
		
	</div>
<%-- End changes for R5 --%>
</form>
<%-- Start changes for R5 --%>
<div class="overlay"></div>
	<div class="alert-box alert-box-exportTaskList">
		<div class="content">
		  	<div id="newTabs" class='wizardTabs'>
				<div class="tabularCustomHead">Export Tasks
					<a href="javascript:void(0);" class="exit-panel nodelete" title="Exit"></a>
				</div>
					<h2 class='padLft' style="padding-left:10px;">Export Tasks</h2>
				<hr class="restoreHeader">					
				<div id="deleteDiv" class="linePadding">
				<div class="pad6 clear promptActionMsg" align="left">Select the organization's tasks to be exported.
					</div>
					<div class='formcontainer'>
					<div class='row'>
						<span class='label'>Organization:</span>
						<span class='formfield'>
						<c:set var="loagencySettingsBean" value="${agencySettingsBean}"></c:set>
							<!--Emergency build changes Start: 4.0.0.2 defect 8379  --> 
							<select id="agencySelectBoxExport" name="agencySelectBoxExport" style="width:280px;">
							<!--Emergency build changes End: 4.0.0.2 defect 8379  --> 
							<option value="" title=""></option>
							<option value="city_org" title="HHS Accelerator">HHS Accelerator</option>
										<c:forEach var="listItems"
												items="${loagencySettingsBean.allAgencyDetailsBeanList}">
										<option value="${listItems.agencyId}~agency_org"
											title="${listItems.agencyName}">${listItems.agencyName}</option>
										</c:forEach>
							</select>
						</span>
					</div>
					</div>
					<div class="buttonholder txtCenter">
						<input type="button" title="Cancel" class="graybtutton exit-panel" id="exportCancelButton" value="   Cancel   " />
						<input type="button" disabled="disabled" title="Export" class="button" id="exportTask" value="Export" onclick="exportTaskList()"/>
					</div>
				</div>
			</div>
		</div>
	</div>
<%-- End changes for R5 --%>
<!-- Form Data Starts -->
<div id="tabs-container">
	<jsp:include page="<%=lsFilePath%>"></jsp:include>
</div>