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
	if( $('#programDupCheck').val() == 'yes' ){
		document.getElementById("programNameChange").disabled = true;
		$('#norm_msg').hide();
		$('#err_msg').show();
	} else {
		document.getElementById("programNameChange").disabled = false;
		$('#norm_msg').show();
		$('#err_msg').hide();
	}
}
</script>

<portlet:defineObjects />
<portlet:actionURL var="confirmProgramNameChangeUrl" id="confirmProgramNameChangeUrl" escapeXml='false'>
	<portlet:param name="submit_action" value="confirmProgramNameChange" />
</portlet:actionURL>

<portlet:actionURL var="modifyProgramNameBackUrl" id="modifyProgramNameBackUrl" escapeXml='false'>
	<portlet:param name="submit_action" value="modifyProgramNameBack" />
</portlet:actionURL>

<div class="overlaycontent">
	<form action="${confirmProgramNameChangeUrl}" method="post" name="confirmProgramNameChangeForm" id="confirmProgramNameChangeForm">
		<input type="hidden" name="confirmProgramNameChangeUrl"		id="confirmProgramNameChangeUrl" value="${confirmProgramNameChangeUrl}"	/>
		<input type="hidden" name="modifyProgramNameBackUrl"		id="modifyProgramNameBackUrl" value="${modifyProgramNameBackUrl}"	/>
		
		<input type="hidden" value="${programIdChange}" name="programIdChange"  id="programIdChange"  >
		<input type="hidden" value="${newProgramNameChange}" name="newProgramNameConfirm"  id="newProgramNameConfirm" >
		<input type="hidden" value="${oldProgramNameChange}" name="oldProgramNameChange"  id="oldProgramNameChange" >
		<input type="hidden" value="${programNameExists}" name="programDupCheck"  id="programDupCheck" >

		<div class="messagedivover" id="messagedivover"> </div>
		<div class='hr'></div>
		<div id="formcontainer1" class="formcontainer">
			<div class="messagediv failed" id="err_msg" style="display: block;">This Program Name already exists. Please click 'Back' and choose a unique Program Name.</div>
			<div class="pad10"  id=norm_msg >Please review the new Program Name and Agency below to confirm.</div>		
			<div id="reqiredDiv" class="reqiredDiv"><label class="required">*</label>Indicates a Required Field</div>
			
			<div class="row">
				<span class="label"><label class="required">*</label>New Program Name:</span>
				<span class="formfield">${newProgramNameChange}</span>
			</div>
			<div class="row">
				<span class="label">Old Program Name:</span>
				<span class="formfield">${oldProgramNameChange}</span>
			</div>
			<div class="row" >
				<span class="label">Agency:</span>
				<select id="agencyDlg" name="agencyDlg" class="input" disabled>
				</select>
			</div>

			<div class='buttonholder'>
				<input type="button" value="Cancel"  title="Cancel" name="cancelDlg1" id="cancelDlg1" class="graybtutton  exit-panel"/>
				<input type="button" value="Back"    title="Back"    name ="backChange" id="backChange" class="graybtutton"/>
	            <input type="submit" value="Confirm" title="Modify Program Name" name="programNameChange" id="programNameChange" disabled="disabled"/>
			</div> 
		</div>
	</form>
</div>


