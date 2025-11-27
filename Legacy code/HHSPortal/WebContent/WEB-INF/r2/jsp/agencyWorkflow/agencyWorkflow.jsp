<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<!--Start Added in R5 -->
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<!--End Added in R5 -->
<%--   BElpw styles added to remove status images from tabs --%>
<script type="text/javascript">
	function formsubmit(str) {
		document.agencyInboxForm.action = $("#inboxActionUrl").val() + '&choosenTab=' + str+ '&taskTabValue=onTabClick';
		document.agencyInboxForm.submit();
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
		<%--End Added in R5 --%>
		$('#agencySelectBox').on('change',function(){
			if($('#agencySelectBox').val() != '') {
				$('#exportTask').removeAttr('disabled');
			}
			else{
				$('#exportTask').attr('disabled','disabled');
			}
		});
	});
</script>
<portlet:defineObjects />
<%
	String lsFilePath = "";
	String lsClassNameForTab1 = "";
	String lsClassNameForTab2 = "";
	String classSe = "class='selected'";
	if (renderRequest.getAttribute("includeFilePath") != null) {
		lsFilePath = (String) renderRequest.getAttribute("includeFilePath");
		if(lsFilePath.endsWith("agencyInbox.jsp")){
			lsClassNameForTab1 = "class='selected'";	
		}else if(lsFilePath.endsWith("agencyTaskManagement.jsp")){
			lsClassNameForTab2 = "class='selected'";			
		}
	}
%>
<portlet:resourceURL var="exportAllTask" id="exportAllTask" escapeXml="false">
</portlet:resourceURL>
<input type = 'hidden' value='${exportAllTask}' id='exportAllTask'/> 
<div id="transactionStatusDiv" class="passed" style="display:none" ></div>	
<!--End Added in R5 -->
<div class='formcontainer'>
<h2>Tasks</h2>
		<d:content section="<%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%>">
			<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
			<input type="hidden" id="screenName" value="Tasks" name="screenName"/>
		</d:content>
		
<input type="hidden" id="inboxActionUrl" value="<portlet:renderURL/>"/>
<input type="hidden" name="contextPathVal" id="contextPathVal" value='${pageContext.servletContext.contextPath}' />
<div class="clear"></div>
<form action="<portlet:actionURL/>" method="post" id = "agencyInbox" name="agencyInboxForm">
	<div class="customtabs">
		<ul class='normalCustomTab'>
			<li <%=lsClassNameForTab1%>><a class="<%=lsClassNameForTab1%>" onclick='javascript:formsubmit("inbox")'>Task Inbox</a></li>
			<li <%=lsClassNameForTab2%>><a class="<%=lsClassNameForTab2%>" onclick='javascript:formsubmit("taskmanager")'>Task Management</a></li>
			<div class="exporttasklink"><a href='#' title='Export all the current task information for your agency' onclick='exportTaskListAgency("${user_organization}");'>Export Task</a></div>
		</ul>
	</div>
	
</form>
<%--  Overlay popup starts --%>
<div class="overlay"></div>
<!--Start Added in R5 -->
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
							<select id="agencySelectBox" name="agencySelectBox" style="width:280px;">
							<option value="" title=""></option>
							<option value="city_org" title="HHS Accelerator">HHS Accelerator</option>
										<c:forEach var="listItems"
												items="${agencySettingsBean.allAgencyDetailsBeanList}">
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
		<a  href="javascript:void(0);" class="exit-panel" title="Exit">&nbsp;</a>
	</div>
<!--End Added in R5 -->
<div class="alert-box-help">
				<div class="tabularCustomHead toplevelheaderHelp"></div>
	            <div id="helpPageDiv"></div>
		  		<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
		</div>
        <div class="alert-box-contact">
			<div id="contactDiv"></div>
	    </div>
<%-- Form Data Starts --%>
<div id="tabs-container">
	<jsp:include page="<%=lsFilePath%>"></jsp:include>
</div>

</div>