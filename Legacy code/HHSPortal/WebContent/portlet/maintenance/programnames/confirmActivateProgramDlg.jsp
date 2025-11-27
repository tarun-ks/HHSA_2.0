<%@page import="org.apache.taglibs.standard.tag.el.core.ForEachTag"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<script type="text/javascript">
//on load function to perform various checks on loading of jsp
function onReady(){

}
</script>

<portlet:defineObjects />
<portlet:actionURL var="confirmProgramActivationUrl" id="confirmProgramActivationUrl" escapeXml='false'>
	<portlet:param name="submit_action" value="confirmProgramActivation" />
</portlet:actionURL>

<div class="overlaycontent">
	<form action="${confirmProgramActivationUrl}" method="post" name="ProgramActivationForm" id="ProgramActivationForm">
		<input type="hidden" value="${targetProgramId}" name="targetProgramId"  id="targetProgramId"  >
		<input type="hidden" value="${targetProgramName}" name="targetProgramName"  id="targetProgramName" >
		<input type="hidden" value="${targetProgramAgency}" name="targetProgramAgency"  id="targetProgramAgency" >

		<div class="messagedivover" id="messagedivover"> </div>
		<div class='hr'></div>
		<div id="formcontainer1" class="formcontainer">
			<div class="pad10">Please review the new Program Name and Agency information below that you would like to activate.</div>		
			<div id="reqiredDiv" class="reqiredDiv"><label class="required">*</label>Indicates a Required Field</div>
			<div class="row">
				<span class="label">Program Name:</span>
				<span class="formfield">${targetProgramName}</span>
			</div>
			<div class="row" >
				<span class="label">Agency:</span>
				<span class="formfield">${targetProgramAgency}</span>
			</div>

			<div class='buttonholder'>
				<input type="button" value="Cancel" title="Cancel" name="cancelDlg" id="cancelDlg" class="graybtutton  exit-panel"/>
	            <input type="submit" value="Activate" title="Activate" name="activateProgram" id="activateProgram" />
			</div> 
		</div>
	</form>
</div>


