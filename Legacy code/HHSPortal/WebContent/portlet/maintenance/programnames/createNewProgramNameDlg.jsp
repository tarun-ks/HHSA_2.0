<%@page import="org.apache.taglibs.standard.tag.el.core.ForEachTag"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<script type="text/javascript">
function onReady(){
		$('#newProgramNameDlg').alphanumeric( { allow: "!,^*%.-\\\'\\\"/ $&()\\[\\]?:;" , nchars:"_"});
	// This will execute when Next button is clicked
		$('#agency option').clone().appendTo('#agencyDlg');
		$('#agencyDlg').val($('#restoredAgency').val());

		$(".alert-box").find('#cancelDlg1').unbind("click").click(function() {
				document.programnamesform.action = $('#programNamePageUrl').val();
				document.programnamesform.submit();
				return false;
		});

		$(".alert-box").find('#moveToStep2').unbind("click").click(function() { 	
			
			$("#newProgramNameForm").validate({
				rules: {
					newProgramNameDlg: {
						required: true,
						minlength: 3,
						allowSpecialChar: ["A", "!\\-,^*%.\\\'\\\"/ $&()\\[\\]?:;0123456789"]
					},
					agencyDlg: {required: true}
				},
				messages: {
					newProgramNameDlg: {required:"<fmt:message key='REQUIRED_FIELDS'/>",   
						                minlength: "<fmt:message key='INPUT_MIN_3_CHAR'/>",
						    			allowSpecialChar: "! Only ! $ % ^ & * () : ; - / \ [ ] \" \' ?  \n are allowed as a special character."						                
						                },
					agencyDlg: {required: "<fmt:message key='REQUIRED_FIELDS'/>"}
				},
				submitHandler: function(form){
					getAgencyName();
					 document.newProgramNameForm.action = $('#createNewProgramUrl').val();
					$(document.newProgramNameForm).ajaxSubmit(options);
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
	                    $("#newProgramTab1").empty();
			 			$("#newProgramTab2").empty();
                        if(data != null || data != ''){
                               $("#newProgramTab2").html(data.detach());
                               callBackInWindow("onReady");
						}
						$("#overlayedJSPContent").html($response);
						$(".overlay").launchOverlay($(".alert-box"), $(".exit-panel"), "890px", null, "onReady");
						// Below classes added when user click Next button following inserting all information in the form.(Step 2)
						$('ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
 						$('#newProgramStep1').removeClass('active').addClass('default');
						$('#newProgramStep2').addClass('activeLast');
					}else{
						$("#newProgramTab2").empty();
 						//$(".messagedivover").html(responsesArr[3]+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
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

		$('#back1').click(function() {
	    		pageGreyOut();
				 var options = 
	    			{	
					   	success: function(responseText, statusText, xhr ) 
						{
							var $response=$(responseText);
                           var data = $response.contents().find(".overlaycontent");
                           	$("#newProgramTab1").empty();
					 			$("#newProgramTab2").empty();
                           if(data != null || data != ''){
                           	$("#newProgramTab1").html(data.detach());
							}
							$("#overlayedJSPContent").html($response);
							$(".overlay").launchOverlay($(".alert-box"), $(".exit-panel"), "890px", null, "onReady");
/* 							$('ul').removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
							$('li').removeClass('ui-state-default ui-corner-top ui-state-hover'); */
	  						$('#newProgramStep1').removeClass('default').addClass('active');
	 						$('#newProgramStep2').removeClass('activeLast');

							removePageGreyOut();
						},
						error:function (xhr, ajaxOptions, thrownError)
						{  
							showErrorMessagePopup();
							removePageGreyOut();
						}
					  };
					document.programnamesform.action = $('#createNewProgramNameBackUrl').val()+'&restoredInput='+ escape($("#newProgramNameConfirm").val());
				  	$(document.programnamesform).ajaxSubmit(options);
				   return false;  
		});
}

function getAgencyName(){
	$("#agencyNameDlg").val(  $( "#agencyDlg option:selected" ).text() );
}

</script>
<portlet:defineObjects />
<portlet:actionURL var="createNewProgramUrl" id="createNewProgramUrl" escapeXml='false'>
	<portlet:param name="submit_action" value="createNewProgramStep" />
</portlet:actionURL>

<div class="overlaycontent" >
	<form  method="post" action="${createNewProgramUrl}" method ="post" name="newProgramNameForm" id="newProgramNameForm">
		<input type="hidden" name="createNewProgramUrl"		id="createNewProgramUrl" value="${createNewProgramUrl}"	/>
		<input type="hidden" name="restoredAgency"		id="restoredAgency" value="${restoredAgency}"	/>
		<input type="hidden" name="escapedNewProgramName"		id="escapedNewProgramName" value="${restoredAgency}"	/>
		
		<div class="formcontainer">
			<div class="messagedivover" id="messagedivover"></div>
			<div class="pad10">Enter a new program name and select Agency, then click next to confirm.</div>
			<div id="reqiredDiv" class="reqiredDiv"><label class="required">*</label>Indicates a Required Field</div>
			<div class="row">
				<span class="label"><label class="required">*</label>Program Name</span> 
				<span class="formfield"> 
<%-- 	            	<input id="newProgramNameDlg" name="newProgramNameDlg" class="proposalConfigDrpdwn"  type="text" value="${restoredInput}"  maxlength="100" > --%>	
					<input id="newProgramNameDlg" name="newProgramNameDlg" class="proposalConfigDrpdwn"  type="text"   maxlength="100" >			
	            </span>
				<span class="error"></span>
			</div>
			<div class='row' id="agencyDivDlg">
				<span class='label'><label class="required">*</label>Agency:</span>
				<span class='formfield'>
					<select id="agencyDlg" name="agencyDlg" class="input" onchange="getAgencyName()">
					</select>
					<input type="hidden" name="agencyNameDlg"		id="agencyNameDlg" value=""	/>
				</span>
				<span class="error"></span>
			</div>
			<div class='buttonholder'> 
				<input type="button" value="Cancel" title="Cancel" name="cancelDlg" id="cancelDlg" class="graybtutton exit-panel" />
				<input type="submit" value="Next" title="Next" name="moveToStep2" id="moveToStep2" />
			</div>
		</div>
	</form>
</div>


