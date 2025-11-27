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
	//$("#restoredInput").val($("#newProgramNameConfirm").val());

	if( $('#programDupCheck').val() == 'yes' ){
		//$('#addNewProgram').hide();
		document.getElementById("addNewProgramFinal").disabled = true;
		$('#norm_msg').hide();
		$('#err_msg').show();
	} else {
		//$('#addNewProgram').show();
		document.getElementById("addNewProgramFinal").disabled = false;
		$('#norm_msg').show();
		$('#err_msg').hide();
	}

}
/*//  $('#submitbutton').hide();   $('#submitbutton').show();*/

</script>

<portlet:defineObjects />
<portlet:actionURL var="confirmNewProgramUrl" id="confirmNewProgramUrl" escapeXml='false'>
	<portlet:param name="submit_action" value="confirmNewProgramStep" />
</portlet:actionURL>
<portlet:actionURL var="createNewProgramNameBackUrl" id="createNewProgramNameBackUrl" escapeXml='false'>
	<portlet:param name="submit_action" value="createProgramNameBack" />
	<portlet:param name="restoredAgency" value="${agencyDlg}" />
</portlet:actionURL>

<div class="overlaycontent">
	<form action="${confirmNewProgramUrl}" method="post" name="newProgramConfirmForm" id="newProgramConfirmForm">
		<input type="hidden" name="confirmNewProgramUrl"		id="confirmNewProgramUrl" value="${confirmNewProgramUrl}"	/>
		<input type="hidden" name="createNewProgramNameBackUrl"		id="createNewProgramNameBackUrl" value="${createNewProgramNameBackUrl}"	/>
	
		<input type="hidden" value="${newProgramNameDlg}" name="newProgramNameConfirm"  id="newProgramNameConfirm"  >
		<input type="hidden" value="${agencyDlg}" name="agencyConfirm"  id="agencyConfirm" >
		<input type="hidden" value="${programNameExists}" name="programDupCheck"  id="programDupCheck" >
		
<%-- 	<portlet:param name="restoredInput" value="${newProgramNameDlg}" /> --%>

		<div class="messagedivover" id="messagedivover"> </div>
		<div class='hr'></div>
		<div id="formcontainer1" class="formcontainer">
			<div class="messagediv failed" id="err_msg" style="display: block;">This Program Name already exists. Please click 'Back' and choose a unique Program Name.</div>
			<div class="pad10" id=norm_msg >Please review the new Program Name and Agency below to confirm.</div>		
			<div id="reqiredDiv" class="reqiredDiv"><label class="required">*</label>Indicates a Required Field</div>
			<div class="row">
				<span class="label">Program Name:</span>
				<span class="formfield">${newProgramNameDlg}</span>
			</div>
			<div class="row" >
				<span class="label">Agency:</span>
				<span class="formfield">${agencyNameDlg}</span>
			</div>

			<div class='buttonholder'>
				<input type="button" value="Cancel" title="Cancel" name="cancelDlg1" id="cancelDlg1" class="graybtutton  exit-panel"/>
				<input type="button" title="Back"    name ="back1" id="back1" value="Back" class="graybtutton"/>	
	            <input type="submit" value="Confirm" title="Add New Program"  name="addNewProgramFinal" id="addNewProgramFinal" disabled="disabled" />
			</div> 
		</div>
	</form>
</div>


