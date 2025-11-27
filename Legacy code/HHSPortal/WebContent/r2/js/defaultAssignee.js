/** 
 * This js handle the default assignee pop-up.
 * This js render the pop-up for all financial task except PCoF.
 */
$(document).ready(
		function() {
			$('a[href="#viewDefaultAssignee"]').click(
					function() {
						pageGreyOut();
						var taskType=$("#hdnTaskTypeDefaultAssignee").val();
						var taskLevel=$("#hdnTaskLevel").val();
						var entityId=$('#defaultAssignmentEntity').val();
						var agencyId=$('#taskAgencyId').val();
						
						var urlAppender = $("#getReassigneeList").val()
								 +'&taskType='+taskType+'&taskLevel='+taskLevel+'&entityId='+entityId+'&agencyId='+agencyId;
						jQuery.ajax({
							type : "POST",
							url : urlAppender,
							success : function(e) {
								$("#fetchRessigneeList").html(e);
								$(".overlay").launchOverlayNoClose(
										$(".alert-box-getReassigneeList"),
										"900px", null, "onReady");
								if(taskType=="Amendment Certification of Funds" || taskType=="Contract Certification of Funds")
									{
									$('tr.rowpos:nth-child(2)').hide();
									}
								$(".exit-panel").click(function() {
									clearAndCloseOverLay();
								});
								removePageGreyOut();
							},
							error : function(data, textStatus, errorThrown) {
								removePageGreyOut();
							}
						});
					});
		});
/**
 * This method is called to Close OverLay
 */
function clearAndCloseOverLay() {
	$(".overlay").closeOverlay();
}

/**
 * This method saves the details of default new assignee
 */
function saveNewDeafultAssignee(){
		pageGreyOut();
		var taskType=$("#hdnTaskTypeDefaultAssignee").val();
		var taskLevel=$("#hdnTaskLevel").val();
		var entityId=$('#defaultAssignmentEntity').val();
		var v_parameter = "&" + $("#reassigneeForm").serialize()+'&taskType='+taskType+'&taskLevel='+taskLevel+'&entityId='+entityId;;
		var urlAppender = $("#submitDefaultAssignment").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(responseText) {
				if(responseText!=null && ""!=responseText){
					$("#ErrorDiv").html(responseText);
					$("#ErrorDiv").show();
				}else {
					clearAndCloseOverLay();
					refresh();
				}
				 removePageGreyOut();
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				 removePageGreyOut();
			}
		});
}
