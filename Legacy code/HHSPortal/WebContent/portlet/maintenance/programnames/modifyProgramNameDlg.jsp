<%@page import="org.apache.taglibs.standard.tag.el.core.ForEachTag"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<script type="text/javascript">
function onReady(){
	    $('#newProgramNameChange').alphanumeric( { allow: "!,^*%.-\\\'\\\"/ $&()\\[\\]?:;" , nchars:"_"});
	// This will execute when Next button is clicked1
	//[Start] copy agency info into the other pages
		$('#agency option').clone().appendTo('#agencyDlg1');
		$('#agency option').clone().appendTo('#agencyDlg');
		$('#agencyDlg1').val($("#targetProgramAgency").val());
		$('#agencyDlg').val($("#targetProgramAgency").val());
	//[End] copy agency info into the other pages

		$(".alert-box-help").find('#cancelDlg1').unbind("click").click(function() {
				document.programnamesform.action = $('#programNamePageUrl').val();
				document.programnamesform.submit();
				return false; 
		});  

		$(".alert-box-help").find('#moveToStep2').unbind("click").click(function() { 	
			$("#modifyProgramNameStep2Form").validate({
				rules: {
					newProgramNameChange: {
						required: true,
						minlength: 3,
						allowSpecialChar: ["A", "!\\-,^*%.\\\'\\\"/ $&()\\[\\]?:;0123456789"]
					},
				},
				messages: {
					newProgramNameChange: {required:"<fmt:message key='REQUIRED_FIELDS'/>",
						               minlength: "<fmt:message key='INPUT_MIN_3_CHAR'/>",
						               allowSpecialChar: "! Only ! $ % ^ & * () : ; / - \ [ ] \" \' ?  \n are allowed as a special character."
					}
				},
				submitHandler: function(form){
					
					$("#newProgramNameTemp").val($("#newProgramNameChange").val());
					 document.modifyProgramNameStep2Form.action = $('#modifyProgramNameStep2Url').val();
					$(document.modifyProgramNameStep2Form).ajaxSubmit(options);
					pageGreyOut();
				},
				errorPlacement: function(error, element) {
				      error.appendTo(element.parent().parent().find("span.error"));
				}
			});

		    var options = 
		    {
		    	success: function(responseText, statusText, xhr ) 
				{
					var responseString = new String(responseText);
					var responsesArr = responseString.split("|");
					if(!(responsesArr[1] == "Error" || responsesArr[1] == "Exception"))
					{
						var $response=$(responseText);
	                    var data = $response.contents().find(".overlaycontent");
	                    $("#changeNameTab1").empty();
			 			$("#changeNameTab2").empty();
                        if(data != null || data != ''){
                               $("#changeNameTab2").html(data.detach());
                               callBackInWindow("onReady");
						}
						$("#overlayedJSPContent").html($response);
						$(".overlay").launchOverlay($(".alert-box-help"), $(".exit-panel"), "890px", null, "onReady");
						// Below classes added when user click Next button following inserting all information in the form.(Step 2)
						$('ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
 						$('#changeNameStep1').removeClass('active').addClass('default');
						$('#changeNameStep2').addClass('activeLast');
					}else{
						$("#changeNameTab2").empty();
 						$(".messagedivover").html(responsesArr[3]+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
			            $(".messagedivover").addClass(responsesArr[4]);
			            $(".messagedivover").show(); 
					}
                    removePageGreyOut();
				},
				error:function (xhr, ajaxOptions, thrownError)
				{
					showErrorMessagePopup();
					removePageGreyOut();
				}
		    };
		});

		$('#backChange').click(function() {
			pageGreyOut();
			 var options = 
				{	
				   	success: function(responseText, statusText, xhr ) 
					{
						var $response=$(responseText);
						var data = $response.contents().find(".overlaycontent");
						$("#changeNameTab1").empty();
				 		$("#changeNameTab2").empty();
						if(data != null || data != ''){
	                   		$("#changeNameTab1").html(data.detach());
						}
						$("#overlayedJSPContent").html($response);
						$(".overlay").launchOverlay($(".alert-box-help"), $(".exit-panel"), "890px", null, "onReady");
						$('ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
						//$('li').removeClass('ui-state-default ui-corner-top ui-state-hover');
  						$('#changeNameStep1').removeClass('default').addClass('active');
 						$('#changeNameStep2').removeClass('activeLast');
						removePageGreyOut();
					},
					error:function (xhr, ajaxOptions, thrownError)
					{  
						showErrorMessagePopup();
						removePageGreyOut();
					}
				  };
			 	
				 document.programnamesform.action = $("#modifyProgramNameBackUrl").val();// + "&restoredVal=" + $('#newProgramNameChange').val();
			  	$(document.programnamesform).ajaxSubmit(options);

		});
}


</script>
<portlet:defineObjects />
<portlet:actionURL var="modifyProgramNameStep2Url" id="modifyProgramNameStep2Url" escapeXml='false'>
	<portlet:param name="submit_action" value="modifyProgramNameStep2" />
</portlet:actionURL>
<portlet:actionURL var="modifyProgramNameStep1Url" id="modifyProgramNameStep1Url" escapeXml='false'>
	<portlet:param name="submit_action" value="modifyProgramNameStep1" />
</portlet:actionURL>

<div class="overlaycontent" >
	<form  method="post" action="${modifyProgramNameStep2Url}" method ="post" name="modifyProgramNameStep2Form" id="modifyProgramNameStep2Form">
		<input type="hidden" name="modifyProgramNameStep2Url"		id="modifyProgramNameStep2Url" value="${modifyProgramNameStep2Url}"	/>
		<input type="hidden" name="modifyProgramNameStep1Url"		id="modifyProgramNameStep1Url" value="${modifyProgramNameStep1Url}"	/>

		<div class="formcontainer">
			<div class="messagedivover" id="messagedivover"></div>
			<div class="pad10">Enter a new program name, then click next to confirm.</div>
			<div id="reqiredDiv" class="reqiredDiv"><label class="required">*</label>Indicates a Required Field</div>

			<div class="row">
				<span class="label"><label class="required">*</label>Program Name</span> 
				<span class="formfield"> 
	            	<input id="newProgramNameChange" name="newProgramNameChange" class="proposalConfigDrpdwn"  type="text" value="${restoredInput}"  maxlength="100" >
	            	<input type="hidden" name="programIdChange" id="programIdChange" value="${programIdChange}"	/>
<%-- 					<input type="hidden" value="${newProgramNameConfirm}" name="newProgramNameConfirm"  id="newProgramNameConfirm" > --%>
	            </span>
				<span class="error"></span>
			</div>

			<div class="row" >
				<span class="label">Current Program Name</span>
				<span class="formfield">${oldProgramNameChange}
						<input type="hidden" name="oldProgramNameChange" id="oldProgramNameChange" value="${oldProgramNameChange}"	/>

				</span>
			</div>

			<div class="row" >
				<span class="label">Agency:</span>
				<select id="agencyDlg1" name="agencyDlg1" class="input" disabled>
				</select>
			</div>
			<div class='buttonholder'>
				<input type="button" value="Cancel" title="Cancel" name="cancelDlg" id="cancelDlg" class="graybtutton exit-panel" />
				<input type="submit" value="Next" title="Next" name="moveToStep2" id="moveToStep2" />
			</div>
		</div>
	</form>
</div>


