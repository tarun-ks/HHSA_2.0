<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<script type="text/javascript" src="../framework/skeletons/hhsa/js/compliance.js"></script>
<script type="text/javascript" src="../resources/js/applicationSummary.js"></script>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<portlet:defineObjects />
<style>
	.errorMessages{
		display:none;
	}
	.formcontainer .row span.label{
		width:32%;
	}
	.formcontainer .row span.formfield{
		width:34% !important;
	}	
	.formcontainer .row span.error{
		 float: left;
	  	 padding: 4px 0;
	     text-align: left; 
		 color:red;
		 width:31%;
	}
</style>
<script type="text/javascript">
	function onReady(){
		var taskId = '<%=request.getAttribute("taskId")%>';
		$("#myformOverlay").validate({
				rules: {
					internalCommentArea: {required: true}
				},
				messages: {
					internalCommentArea: {required:"<fmt:message key='REQUIRED_FIELDS'/>"}
				},
				submitHandler: function(form){
					document.myformOverlay.action=document.myformOverlay.action+'&status=Suspended'+'&next_action=forcefullySuspend'+'&taskid='+taskId;
					document.myformOverlay.submit();
					pageGreyOut();
				},
				errorPlacement: function(error, element) {
				      error.appendTo(element.parent().parent().find("span.error"));
				}
			});
	}

	$(".alert-box-suspend").find('#closeButtonId').click(function() {
		document.getElementById("linkDiv").style.display="block";
		$(".overlay").closeOverlay();
		document.myform.action = taskdetailAction;
		return false;
	});
		
</script>
<div class="overlaycontent">
<form name="myformOverlay" id="myformOverlay" action="<portlet:actionURL/>" method ="post" >
			
        	<div class="row"><span id="headerText">Please enter any internal comments associated with this suspension. Only the HHS Accelerator team will be able to read this comment:</span></div>
            <div class="row"> 
            	<label class="required"></label><textarea id="internalCommentArea" name="internalCommentArea" cols="" rows="" class="input" style="width:350px;" onkeyup="setMaxLength(this,500)" onkeypress="setMaxLength(this,500)" ></textarea>
           	<span class="error"></span>
            </div>
            <div class="buttonholder">
                <input type="button" class="graybtutton" value="Cancel" title="Cancel" id="closeButtonId"/>
                <input type="submit" value="Suspend" title="Suspend" id="submitButtonId"/>
            </div>
            </form>
</div>
        
