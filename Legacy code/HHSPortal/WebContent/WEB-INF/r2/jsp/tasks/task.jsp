<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%-- Release 5 changes Starts --%>
<%@ page errorPage="/error/errorpage.jsp"%>
<%@ taglib prefix="fmtTask" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript">
</script>
<fmtTask:setBundle basename="com/nyc/hhs/properties/messages" />
<portlet:defineObjects />
<portlet:resourceURL var='finishTaskApprove' id="finishTaskApprove"
	escapeXml='false'>
</portlet:resourceURL>
<portlet:resourceURL var='finishTaskDefferred' id="finishTaskDefferred"
	escapeXml='false'>
</portlet:resourceURL>
<portlet:resourceURL var='finishTaskReturn' id="finishTaskReturn"
	escapeXml='false'>
</portlet:resourceURL>
<portlet:resourceURL var='finishTaskWithdraw' id="finishTaskWithdraw"
	escapeXml='false'>
</portlet:resourceURL>
<portlet:resourceURL var='finishTaskRejected' id="finishTaskRejected"
	escapeXml='false'>
</portlet:resourceURL>
<portlet:actionURL var='redirectToTask' escapeXml='false'>
	<portlet:param name='taskcontrollerAction' value='redirectToTask' />
</portlet:actionURL>
<portlet:actionURL var='reAssignTask' escapeXml='false'>
	<portlet:param name='taskcontrollerAction' value='reAssignTask' />
</portlet:actionURL>
<portlet:resourceURL var='saveComments' id="saveComments"
	escapeXml='false'>
</portlet:resourceURL>
<portlet:resourceURL var='viewCommentsHistory' id='viewCommentsHistory'
	escapeXml='false'>
</portlet:resourceURL>
<input type='hidden' value='${finishTaskApprove}'
	id='hiddenTaskHeaderURL' />
<input type='hidden' value='${saveComments}' id='hiddenTaskFooterURL' />
<input type='hidden' value='${viewCommentsHistory}'
	id='hiddenViewCommentsURL' />

<form name="taskReassigneeform" id="taskReassigneeform" action="<portlet:actionURL/>" method ="post" >
<portlet:resourceURL var="getReassigneeList" id="getReassigneeList"
	escapeXml="false">
</portlet:resourceURL>
<input type='hidden' value='${getReassigneeList}' id='getReassigneeList' />
<%-- added for r5 getContractSharedList --%>
<div class="overlay"></div>
<div class="alert-box alert-box-getReassigneeList">
<a href="javascript:void(0);" class="exit-panel upload-exit" title="Exit">&nbsp;</a>
<div id="fetchRessigneeList"></div>
</div>

<script>
var publicCommentErrorMsg = "<fmtTask:message key='publicCommentErrorMsg'/>";
var internalCommentErrorMsg = "<fmtTask:message key='internalCommentErrorMsg'/>";
var internalAgencyCommentErrorMsg = "<fmtTask:message key='internalAgencyCommentErrorMsg'/>";
var awardEpinNotAssignErrorMsg = "<fmtTask:message key='awardEpinNotAssignErrorMsg'/>";
var lbIsTAskAssign = "${detailsBeanForTaskGrid.isTaskAssigned}";
var lbIsScreenLocked = "${accessScreenEnable}";
var lsEntityTypeLevel = "${detailsBeanForTaskGrid.entityTypeTabLevel}";

var contextPathVariable = "<%=request.getContextPath()%>";
	var taskLevel = "";
	$(document)
			.ready(
					function() {

						//Updated for R4: Added check to Disable/Enable TLC SAVE Comments BUTTON
						if ($("#publicCommentArea").attr('readonly') == 'readonly'
								|| $("#publicCommentArea").attr('disabled') == 'disabled'
								|| $("#publicCommentArea").attr('disabled') == true) {
							$("textarea[id^=publicCommentAreaTabLevel]").each(
									function() {
										$(this).attr('readonly', 'readonly');
									});
						}
						if ($("#saveComment").attr('readonly') == 'readonly'
								|| $("#saveComment").attr('disabled') == 'disabled'
								|| $("#saveComment").attr('disabled') == true) {
							$(
									"input[type='button'][id^=saveCommentTabLevelTLC_]")
									.each(function() {
										$(this).attr('disabled', true);
									});
						}
						if ($("#internalCommentArea").attr('readonly') == 'readonly'
								|| $("#internalCommentArea").attr('disabled') == 'disabled'
								|| $("#internalCommentArea").attr('disabled') == true) {
							$("textarea[id^=internalCommentAreaTabLevel]")
									.each(function() {
										$(this).attr('readonly', 'readonly');
									});
						}
						if (document.getElementById("hdnEntityTypeTabLevel"
								+ lsEntityTypeLevel) != null
								&& $(
										"#hdnEntityTypeTabLevel"
												+ lsEntityTypeLevel).val() != "") {
							$(
									"#commentsTabsTabLevel" + lsEntityTypeLevel
											+ " li")
									.removeClass(
											'ui-state-default ui-corner-top ui-tabs-selected ui-state-active');
							$("#commentsTabsTabLevel" + lsEntityTypeLevel)
									.tabs();
							$("#commentWrapperIdTabLevel" + lsEntityTypeLevel)
									.click();
							document.getElementById("hdnEntityTypeTabLevel"
									+ lsEntityTypeLevel).value = "";
						} else {
							$("#commentsTabs li")
									.removeClass(
											'ui-state-default ui-corner-top ui-tabs-selected ui-state-active');
							$("#commentsTabs").tabs();
							if (document.getElementById("documentWrapperId") != null) {
								$("#documentWrapperId").click();
							} else {
								$("#commentWrapperId").click();
							}
						}
						if (document.getElementById("hdnTaskLevel") != null) {
							taskLevel = $("#hdnTaskLevel").val();
						}
					});

	//  * This method is upated for release 3.8.0 defect#6483
	// This function is called on click of Finish task button on Task Header 
	function finishTask() {
		$("#taskErrorDiv").html("");
		$("#taskErrorDiv").hide();
		if (validateComment()) {
			var taskStatus = $("#finishtaskchild").val();
			if ("Approved" == taskStatus) {
				$("#hiddenTaskHeaderURL").val("${finishTaskApprove}");
			} else if ("Deferred" == taskStatus) {
				$("#hiddenTaskHeaderURL").val("${finishTaskDefferred}");
			} else if ("Rejected" == taskStatus) {
				$("#hiddenTaskHeaderURL").val("${finishTaskRejected}");
			} else if ("Returned for Revision" == taskStatus) {
				$("#hiddenTaskHeaderURL").val("${finishTaskReturn}");
			} else if ("Cancel" == taskStatus) {
				$("#hiddenTaskHeaderURL").val("${finishTaskReturn}");
			} else if ("Withdraw" == taskStatus) {
				$("#hiddenTaskHeaderURL").val("${finishTaskWithdraw}");
			} else {
				$("#hiddenTaskHeaderURL").val("${finishTaskApprove}");
			}
			commentsChange = false;
			finishTaskAjax($("#hiddenTaskHeaderURL").val());
		} else {
			$("#taskErrorDiv").html(invalidResponseMsg);
			$("#taskErrorDiv").show();
		}
	}
	//This function is called on click of reAssignTask task button on Task Header 
	function reAssignTask() {
		var $newLaunchflag = false;
		if($('#hdnNewLaunch').val() == 'true'){
			$newLaunchflag =true;
		}
		if( $newLaunchflag && $('#report_icon a').attr('href').indexOf("reportType=financials")>0 && $("#hdnTaskTypeDefaultAssignee").val() != 'Procurement Certification of Funds' && $("#askFlag").val() !="Y")
		{	
		pageGreyOut();
		var urlAppender = $("#setDefaultUser").val() + "&tasklevel=" + $("#hdnTaskLevel").val()+"&tasktype=" + $("#hdnTaskTypeDefaultAssignee").val() + "&assignedTo="+ $("#assignUser option:selected").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			success : function(e) {
				$("#getUserAssigneeList").html(e);
/* 				if($("#askFlag").val() =="Y"){
					$('#askAgain').siblings('span:last-child').hide();
					$('#askAgain').hide();
				} */
				$('#setDefaultManually').hide();
				$('.checkDefaultTask').click(function() {
					$('input[id=restoreButton]').prop('disabled', false);
					if ($(this).attr("value") == 'Yes') {
						$('#setDefaultManually').hide();
						$('#keepCurrentDefault').attr('checked', false);
						$('#askAgain').attr('checked', false);
					} else {
						$('#setDefaultManually').show();
						$('#keepCurrentDefault').attr('checked', true);
					}
				});
				$('#assignTo').text($("#assignUser option:selected").text());
				$('#taskType').text($("#hdnTaskTypeDefaultAssignee").val());
				$('#taskLevel').text("Level "+$("#hdnTaskLevel").val());
				$(".overlay").launchOverlayNoClose($(".alert-box-getDefaultAssignee"),
						"600px", null, "onReady");
				$(".exit-panel").click(function() {
					clearAndCloseOverLay();
					$('.checkDefaultTask').attr("checked", false);
					$('#restoreButton').attr('disabled','disabled');
				});
				removePageGreyOut();
			},
			error : function(data, textStatus, errorThrown) {
				removePageGreyOut();
			}
		});
			}
		else{
			$("#taskErrorDiv").html("");
			$("#taskErrorDiv").hide();
			if(validateComment()){
			pageGreyOut();
			$("#hiddenTaskHeaderURL").val("${reAssignTask}");
			var reAssignUserId = document.getElementById("assignUser");
			var reAssignUser = reAssignUserId.options[reAssignUserId.selectedIndex].text;
			$("#reassigntouserText").val(reAssignUser);
			if(document.getElementById("internalCommentArea")!=null){
				document.getElementById("hdnInternalComment").value=$("#internalCommentArea").val();
			}if(document.getElementById("publicCommentArea")!=null){
				document.getElementById("hdnProviderComment").value=$("#publicCommentArea").val();
			}
			$("#taskHeaderForm").attr("action",$("#hiddenTaskHeaderURL").val());
			document.taskHeaderForm.submit();
			}else{
				$("#taskErrorDiv").html(invalidResponseMsg);
				$("#taskErrorDiv").show();
			}
		}
	}

	//This function is called on click of reAssignTask task button on Task Header 
	function ressignCallConfirm(){

		$("#taskErrorDiv").html("");
		$("#taskErrorDiv").hide();
		if(validateComment()){
		pageGreyOut();
		$("#hiddenTaskHeaderURL").val("${reAssignTask}");
		var reAssignUserId = document.getElementById("assignUser");
		var reAssignUser = reAssignUserId.options[reAssignUserId.selectedIndex].text;
		$("#reassigntouserText").val(reAssignUser);
		if(document.getElementById("internalCommentArea")!=null){
			document.getElementById("hdnInternalComment").value=$("#internalCommentArea").val();
		}if(document.getElementById("publicCommentArea")!=null){
			document.getElementById("hdnProviderComment").value=$("#publicCommentArea").val();
		}
		$("#taskHeaderForm").attr("action",$("#hiddenTaskHeaderURL").val());
		document.taskHeaderForm.submit();
		}else{
			$("#taskErrorDiv").html(invalidResponseMsg);
			$("#taskErrorDiv").show();
		}
	}
	
		//Added for R4: This function is called on click of save  button on Tab level to save the comments 
	function saveCommentsTabLevel(entityTypeTabLevel) {
		$("#taskErrorDiv").html("");
		$("#taskErrorDiv").hide();
		$("#errorGlobalMsg").html("");
		$("#errorGlobalMsg").hide();
		if (validateComment()) {
			//Start R5: UX module, clean AutoSave Data
			deleteAutoSaveData();
			//End R5: UX module, clean AutoSave Data
			$("#hiddenTaskFooterURL").val("${saveComments}");
			commentsChange = false;//todo have to change
			var underScoreSplitArray = entityTypeTabLevel.split("_");
			//This function removes particular value if array contains passed value
			changeOnGridCommentsArr = removeArrValue(changeOnGridCommentsArr,
					underScoreSplitArray[2]);
			var v_parameter = "publicCommentArea="
					+ convertSpecialChar($(
							"#publicCommentAreaTabLevel" + entityTypeTabLevel)
							.val())
					+ "&internalCommentArea="
					+ convertSpecialChar($(
							"#internalCommentAreaTabLevel" + entityTypeTabLevel)
							.val()) + "&entityTpeTabLevel="
					+ entityTypeTabLevel;
			if (document.getElementById("publicCommentAreaTabLevel"
					+ entityTypeTabLevel) == null) {
				v_parameter = "internalCommentArea="
						+ convertSpecialChar($(
								"#internalCommentAreaTabLevel"
										+ entityTypeTabLevel).val())
						+ "&entityTpeTabLevel=" + entityTypeTabLevel;
			} else if (document.getElementById("internalCommentAreaTabLevel"
					+ entityTypeTabLevel) == null) {
				v_parameter = "publicCommentArea="
						+ convertSpecialChar($(
								"#publicCommentAreaTabLevel"
										+ entityTypeTabLevel).val())
						+ "&entityTpeTabLevel=" + entityTypeTabLevel;
			}
			var url = $("#hiddenTaskFooterURL").val() + "&tabLevel=true";
			// fix done as a part of release 3.1.2 defect 6420 - start
			if (typeof ($('#invoiceId').val()) != 'undefined') {
				url = url + '&invoiceIdAtPage=' + $('#invoiceId').val();
			}
			// fix done as a part of release 3.1.2 defect 6420 - end
			pageGreyOut();
			var urlAppender = url;
			jQuery.ajax({
				type : "POST",
				url : urlAppender,
				data : v_parameter,
				success : function(e) {
					// $("#commentsHistoryWrapper").html(e);
					//fix done as a part of release 3.1.2 defect 6420
					if (e != null && e != '' && e != 'null') {
						$("#invoiceErrorMsg" + entityTypeTabLevel).html(e);
					}
					removePageGreyOut();
				},
				beforeSend : function() {
				}
			});
		} else {
			if (document.getElementById("taskErrorDiv") != null) {
				$("#taskErrorDiv").html(invalidResponseMsg);
				$("#taskErrorDiv").show();
			} else {
				$("#errorGlobalMsg").html(invalidResponseMsg);
				$("#errorGlobalMsg").show();
			}
		}
	}

	//This function is called on click of save  button on Task Footer to save the comments 
	function saveComments() {
		$("#taskErrorDiv").html("");
		$("#taskErrorDiv").hide();
		$("#errorGlobalMsg").html("");
		$("#errorGlobalMsg").hide();
		if (validateComment()) {
			//Start R5: UX module, clean AutoSave Data
			deleteAutoSaveData();
			//End R5: UX module, clean AutoSave Data
			$("#hiddenTaskFooterURL").val("${saveComments}");
			commentsChange = false;
			var v_parameter = "publicCommentArea="
					+ convertSpecialChar($("#publicCommentArea").val())
					+ "&internalCommentArea="
					+ convertSpecialChar($("#internalCommentArea").val());
			if (document.getElementById("publicCommentArea") == null) {
				v_parameter = "internalCommentArea="
						+ convertSpecialChar($("#internalCommentArea").val());
			} else if (document.getElementById("internalCommentArea") == null) {
				v_parameter = "publicCommentArea="
						+ convertSpecialChar($("#publicCommentArea").val());
			}
			var url = $("#hiddenTaskFooterURL").val();
			// fix done as a part of release 3.1.2 defect 6420 - start 
			if (typeof ($('#invoiceId').val()) != 'undefined') {
				url = url + '&invoiceIdAtPage=' + $('#invoiceId').val();
			}
			// fix done as a part of release 3.1.2 defect 6420 - end  
			pageGreyOut();
			var urlAppender = url;
			jQuery.ajax({
				type : "POST",
				url : urlAppender,
				//changes done for release 3.6.0
				dataType : 'html',
				data : v_parameter,
				success : function(e) {
					// $("#commentsHistoryWrapper").html(e);
					if (e != null && e != '' && e != 'null') {
						//fix done as a part of release 3.1.2 defect 6420
						$("#invoiceErrorMsg").html(e);
					}
					removePageGreyOut();
				},
				beforeSend : function() {
				}
			});
		} else {
			if (document.getElementById("taskErrorDiv") != null) {
				$("#taskErrorDiv").html(invalidResponseMsg);
				$("#taskErrorDiv").show();
			} else {
				$("#errorGlobalMsg").html(invalidResponseMsg);
				$("#errorGlobalMsg").show();
			}
		}
	}
	//This function is called on change of task status dropdown to enable or disable the task button
	function enableFinishButton() {
		$("#taskErrorDiv").html("");
		$("#taskErrorDiv").hide();
		if (document.getElementById("finishtaskchild").selectedIndex != 0) {
			document.getElementById("finish").disabled = false;
		} else {
			document.getElementById("finish").disabled = true;
		}
	}

	//This function is called on change of Reassign dropDown to enable or disable the Reassign Button button
	function enableReassignButton() {
		$("#taskErrorDiv").html("");
		$("#taskErrorDiv").hide();
		if (document.getElementById("assignUser").selectedIndex != 0) {
			document.getElementById("ReassignButton").disabled = false;
		} else {
			document.getElementById("ReassignButton").disabled = true;
		}
	}

	//Added for R4: This function is called on click of view Comments History tab on tab level
	function fetchCommentsHistoryTabLevel(entityTypeTabLevel) {
		pageGreyOut();
		var v_parameter = "hdnTaskType=" + $("#hdnTaskType").val()
				+ "&hdnFetchHistoryQuery=" + $("#hdnFetchHistoryQuery").val()
				+ "&entityTpeTabLevel=" + entityTypeTabLevel;
		var urlAppender = $("#hiddenViewCommentsURL").val() + "&tabLevel=true";
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(e) {
				$("#commentsHistoryWrapperTabLevel" + entityTypeTabLevel).html(
						e);
				removePageGreyOut();
			},
			beforeSend : function() {
			}
		});
	}

	//This function is called on click of view Comments History tab on task footer
	function fetchCommentsHistory() {
		pageGreyOut();
		var v_parameter = "hdnTaskType=" + $("#hdnTaskType").val()
				+ "&hdnFetchHistoryQuery=" + $("#hdnFetchHistoryQuery").val();
		;
		var urlAppender = $("#hiddenViewCommentsURL").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(e) {
				$("#commentsHistoryWrapper").html(e);
				removePageGreyOut();
			},
			beforeSend : function() {
			}
		});
	}

	//This function is called on click of view Comments History tab on task footer
	function finishTaskAjax(url) {
		pageGreyOut();
		//Added in R6: Fix for defect 8552
		var taskType = $("#hdnTaskType").val();
		//Added in R6: end
		var period = "";
		if (document.getElementById("periodId") != null) {
			period = document.getElementById("periodId").value;
		}
		var v_parameter = "hdnTaskType=" + $("#hdnTaskType").val()
				+ "&publicCommentArea="
				+ convertSpecialChar($("#publicCommentArea").val())
				+ "&internalCommentArea="
				+ convertSpecialChar($("#internalCommentArea").val())
				+ "&period=" + period;
		if (document.getElementById("publicCommentArea") == null) {
			v_parameter = "hdnTaskType=" + $("#hdnTaskType").val()
					+ "&internalCommentArea="
					+ convertSpecialChar($("#internalCommentArea").val())
					+ "&period=" + period;
		} else if (document.getElementById("internalCommentArea") == null) {
			v_parameter = "hdnTaskType=" + $("#hdnTaskType").val()
					+ "&publicCommentArea="
					+ convertSpecialChar($("#publicCommentArea").val())
					+ "&period=" + period;
		}
		//Added in R6: Fix for defect 8552 to send form data
		if ("taskReturnedPaymentReview" == taskType){
			v_parameter = "hdnTaskType=" + taskType
			+ "&internalCommentArea="
			+ convertSpecialChar($("#internalCommentArea").val())
			+ "&period=" + period+"&returnPaymentDetailId="+$("#returnedPaymentId").val()+"&checkNumber="+$("#checkNumber").val()
		    +"&agencyTracking="+$("#agencyTracking").val()+"&checkAmount="+$("#checkAmount").val()+"&checkDate="+$("#checkDate").val()
		    +"&receivedDate="+$("#receivedDate").val()+"&descriptionInput="+$("#descriptionInput").val();
		}
		//Start R8.6.0 QC_9499 Multi-Tab Browsing letting Invoice and Advance tasks to be Approved Multiple times causing Duplicate Payments
		if( ( document.getElementById("<%=com.nyc.hhs.util.HHSTokenUtil.getTokenKey()%>") )!=null ) {
			var hhsToken = document.getElementById("<%=com.nyc.hhs.util.HHSTokenUtil.getTokenKey()%>").value;
			if (hhsToken != null) {
				v_parameter = v_parameter + "&<%=com.nyc.hhs.util.HHSTokenUtil.getTokenKey()%>=" + hhsToken;
			}
		}
		//End R8.6.0 QC_9499 Multi-Tab Browsing letting Invoice and Advance tasks to be Approved Multiple times causing Duplicate Payments
		
		//Added in R6: end
		var urlAppender = url;
	 	jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(e) {
				var errorArray = e.split(":");
				if (e.indexOf("pageError") != -1) {
					$("#taskErrorDiv").html(errorArray[1]);
					$("#taskErrorDiv").show();
					removePageGreyOut();
				} else {
					$("#hiddenTaskHeaderURL").val("${redirectToTask}");
					if (e.indexOf("taskError") != -1) {
						$("#taskHeaderForm").attr(
								"action",
								$("#hiddenTaskHeaderURL").val() + "&error="
										+ errorArray[1]);
					} else {
						$("#taskHeaderForm").attr("action",
								$("#hiddenTaskHeaderURL").val() + "&error=");
					}
					$("#taskHeaderForm").submit();
				}

			},
			beforeSend : function() {
			}
		}); 
	}
	//trim the String
	function trim(stringToTrim) {
		return stringToTrim.replace(/^\s+|\s+$/g, "");
	}

	//change the status of Flag
	function setChangeFlag() {
		commentsChange = true;
	}

	//Added for R4: change the status of Flag for tab level
	function setChangeFlagTabLevel(tabSubBudgetId) {
		if (!isArrayContainsDuplicateValue(changeOnGridCommentsArr,
				tabSubBudgetId)) {
			changeOnGridCommentsArr.push(tabSubBudgetId);
		}
	}

	//this function sets the max limit
	//updated in R5
	function setMaxLength(obj, maxlimit) {
		if(($(obj).val().length + $(obj).val().split("\n").length-1)  > maxlimit){
			$(obj).val($(obj).val().substring(0, maxlimit - $(obj).val().split("\n").length + 1));
			return false;
		}
	}

	//this function show or hide the save button
	function showHideSave(liId) {
		if (liId == 'documentWrapperId') {
			if (document.getElementById("tempDivOverday") != null) {
				$("#tempDivOverday").removeClass("overlay");
			}
		} else {
			if (document.getElementById("tempDivOverday") != null) {
				$("#tempDivOverday").addClass("overlay");
			}
		}
		if (liId == '' || liId == 'documentWrapperId') {
			$("#saveComment").hide();
		} else {
			$("#saveComment").show();
		}
	}

	//Added for R4:this function show or hide the tab level comments save button 
	function showHideSaveTabLevel(entityTypeTabLevel, liId) {
		if (liId == '' || liId == 'documentWrapperId') {
			$("#saveCommentTabLevel" + entityTypeTabLevel).hide();
		} else {
			$("#saveCommentTabLevel" + entityTypeTabLevel).show();
		}
	}

	//This function removes particular value if array contains passed value
	function removeArrValueForComments(arr, value) {
		for ( var i = 0; i < arr.length; i++) {
			if (arr[i].indexOf(value) != -1) {
				arr.splice(i, 1);
				break;
			}
		}
		return arr;
	}

	//This function does not allow duplicates in the array
	function isArrayContainsDuplicateValue(arr, findValue) {
		var i = arr.length;
		while (i--) {
			if (arr[i].indexOf(findValue) != -1)
				return true;
		}
		return false;
	}
	
	/**
	 * This function is added in Release 6. It is called on clicking Finish button
	 present in Returned Payment Task Screen.
	 **/
	function finishTaskForReturnedPayment() {
		$("#taskErrorDiv").html("");
		$("#taskErrorDiv").hide();
		var taskType = $("#hdnTaskType").val();
		if (validateComment()) {
			var taskStatus = $("#finishtaskchild").val();
			if ("Cancel" == taskStatus && "taskReturnedPaymentReview" == taskType) {
				pageGreyOut();
				$(".overlay").launchOverlay(
						$(".alert-box-Cancel-ReturnedPayment"),
						$(".exit-panel"), "400px", "auto", null);
				removePageGreyOut();
			} else {
				if ("Approved" == taskStatus) {
					$("#hiddenTaskHeaderURL").val("${finishTaskApprove}");
				} else if ("Deferred" == taskStatus) {
					$("#hiddenTaskHeaderURL").val("${finishTaskDefferred}");
				} else if ("Rejected" == taskStatus) {
					$("#hiddenTaskHeaderURL").val("${finishTaskRejected}");
				} else if ("Returned for Revision" == taskStatus) {
					$("#hiddenTaskHeaderURL").val("${finishTaskReturn}");
				} else if ("Withdraw" == taskStatus) {
					$("#hiddenTaskHeaderURL").val("${finishTaskWithdraw}");
				} else {
					$("#hiddenTaskHeaderURL").val("${finishTaskApprove}");
				}
				commentsChange = false;
				//Updated in R6: Fix for defect 8552
				finishTaskAjax($("#hiddenTaskHeaderURL").val());
				//Updated in R6: end
			}
		} else {
			$("#taskErrorDiv").html(invalidResponseMsg);
			$("#taskErrorDiv").show();
		}
	}
	/**
	 * This function is added in Release 6. It is called for cancelling task at level 1.
	 **/
	function cancelWorkFlow(){
		$("#hiddenTaskHeaderURL").val("${finishTaskReturn}");
		commentsChange = false;
		finishTaskAjax($("#hiddenTaskHeaderURL").val());
	}
</script>
