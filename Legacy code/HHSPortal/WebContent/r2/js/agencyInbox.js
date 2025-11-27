/**
 * This file contains methods that will handle the
 * functionality of agency task inbox
 */
suggestionVal = "";
isValid = false;
var taskArray = new Array();
var selected = new Array();
var onload = "";
var selectedVal = 0;
// changes for R5 start
var askFlagsOfChecked = {};
var hideDontAskFlag = false;
/**
 *  changes for R5 ends
 *	This method call on page load
 */
$(document).ready(
/**
* This method handles the typeheadsearch for competition pool
*  Updated Method in R4
**/
              function() {
                     $(".exit-panel").click(function() {
                           clearAndCloseOverLay();
                     });
                     
                     if($(this).is(":checked") && $(this).attr("financialTask") == "true"){
                           if(!($("#enableBulkAssign") != null && $("#enableBulkAssign").val() == "true"))
                           {
                           $(".taskCheckBox").each(function(){                                         
                                         if(!$(this).is(":checked")){
                                                $(this).attr("disabled", true);
                                         }
                                  });
                           }
                     }
                     //R5 end
                     if($("#competitionPoolTitle").val() == "" && $("#procurementId").val() != "P"){
                           $("#competitionPoolTitle").attr("disabled", true);
                     }else{
                           typeHeadSearch($('#competitionPoolTitle'), $("#hiddenFetchTypeAheadNameList")
                                         .val()+ "&QueryId=fetchCompetitionPoolTitleList&key=COMPETITION_POOL_ID&value=COMPETITION_POOL_TITLE&procurementTitle="+$("#procurementTitle").val(), null, null, null, "competitionPoolId");
                     }
                     
                     typeHeadSearch($('#procurementTitle'), $("#hiddenFetchTypeAheadNameList").val()+ "&QueryId=fetchProcurementContractTitleList&key=PROCUREMENT_TYPE&value=PROCUREMENT_TITLE", 
                                  "competitionPoolTitle", null, null, "procurementId", "callBackProcurementTitleOnSelect");
                     $('#procurementTitle').keyup(function(evt) {
                                  if (suggestionVal.length > 0 && 3 <= $('#procurementTitle').val().length)
                                  var keyCode = evt ? (evt.which ? evt.which : evt.keyCode)
                                                : event.keyCode;
                                  if (keyCode != 13) {
                                         $("#procurementId").val("");
                                         $("#competitionPoolTitle").val("");
                                         $("#competitionPoolId").val("");
                                  }
                           });
                     
                     //this function is used to sort
                     $("#tasktype").html($("#tasktype>option", $(this)).sort(function(a, b) { 
                         var arel = $(a).html();
                         var brel = $(b).html();
                         return arel == brel ? 0 : arel < brel ? -1 : 1; 
                     }));
                     $(".taskCheckBox").click(function(){
                           enableSubmit();
                           //Release 5
                           getDefaultAssignmentDetails(this);
                                  removePageGreyOut();
                                  //Release 5
                     });// saves the selected element on the screen
                     $("#tasktype").change(
                                  function() {
                                         var selectedValue = $(this).val();
                                         if (selectedValue != " ") {
                                                var statusData = $(
                                                              ".hiddenStatusMap[key='" + selectedValue
                                                                           + "']").val();
                                                
                                                $("#status").html("<option value=''></option>" + statusData);
                                                $("#status").attr("disabled", false);
                                                if(onload == "onload"){
                                                       $("#status option[value=\""+$("#dropDownValuePrevstatus").val()+"\"]").attr("selected", "selected");
                                                       onload = "";
                                                }
                                         } else {
                                                $("#status").attr("disabled", true);
                                         }
                                  });
                     typeHeadSearch($('#providername'),  $("#contextPathSession").val() + '/AutoCompleteServlet.jsp?selectedpage=inbox',null,"typeHeadCallBackProvider",null);
                     preSelectFilter();
                     removePageGreyOut();
                     if($("#tasktype").val() == "Evaluate Proposal"){
                           $("#selectAll").attr("disabled", "disabled");
                     }
              });

/**
 * this method will preselect the values in filter
 * */
function preSelectFilter(){
       onload = "onload";
       $("#tasktype option[value=\""+$("#dropDownValuePrevtasktype").val()+"\"]").attr("selected", "selected");
       $("#tasktype").change();
       $("#programname option[value=\""+$("#dropDownValuePrevprogramname").val()+"\"]").attr("selected", "selected");
       selectedVal = $("#tasktype").prop("selectedIndex");
}

/**
 *  This will execute when Filter Documents tab is clicked or closed
 *  */
function setVisibility(id, visibility) {
       if ($("#" + id).is(":visible")) {
              document.myinboxform.reset();
       }
       $("#" + id).toggle();
       if($("#dropDownValuePrevtasktype").val()=="")
       {
              $("#tasktype>option").eq(0).attr('selected', 'selected');
              document.getElementById("filtersBtn").disabled = true;
              document.getElementById("status").disabled = true;
       }
}

/**
* Method submit the form on click of column header click and pass column name
* to sort.
* 
 */

function sort(columnName) {
       document.myinboxform.reset();
       $("#myinboxform")
                     .attr(
                                  "action",
                                  $("#myinboxform").attr("action")
                                                + "&choosenTab=inbox&submit_action=agencySorting&sortGridName=agencyTaskInbox"
                                                + sortConfig(columnName));
       document.myinboxform.submit();
}
/**
* Method submit the form on click of filer button.
* 
 */
function filtertask() {
       
       
       var procurementTitle = $("#procurementTitle").val();
       if (null != procurementTitle && procurementTitle != '') {
              var length = procurementTitle.length;
              if (length < 5) {
                     $('#procurementTitle').parent().next().html(
                                  "! You must enter 5 or more characters");
                     return false;
              }
       }
       
       var isValid = true;
       $("input[type='text']").each(function() {
              if ($(this).attr("validate") == 'calender') {
                     if (!verifyDate(this)) {
                           isValid = false;
                     }
              }
       });

       if ($("#providername").val().length > 0 && suggestionVal.length > 0) {
              var isValidLocal = isAutoSuggestValid($("#providername").val(),
                           suggestionVal);
              if (!isValidLocal)
                     isValid = false;
       }
       
       var today = new Date();
       $('#myinboxform input:text').each(function() {
              if ($(this).attr("validate")=='calender') {
                     var current = new Date($(this).val());
                     if(current>today)
                           {
                           isValid = false;
                           $(this).parent().next().html(
                           "! Invalid Date. Please enter a date in the past.");
                           }
              }
       });
       
       var startDate = new Date($("#datefrom").val());
       var endDate = new Date($("#dateto").val());
       if (!checkStartEndDatePlanned(startDate, endDate)) {
              $("#dateto").parent().next().html(
                           "! End Date can not be less than Start Date.");
              return false;
       }
       
       var startDate = new Date($("#dateassignedfrom").val());
       var endDate = new Date($("#dateassignedto").val());
       if (!checkStartEndDatePlanned(startDate, endDate)) {
              $("#dateto").parent().next().html(
                           "! End Date can not be less than Start Date.");
              return false;
       }

       if (isValid) {
              pageGreyOut();
              document.myinboxform.action = document.myinboxform.action
                           + "&filteristrue=yes&submit_action=inboxFilter&choosenTab=inbox";
              document.myinboxform.submit();
       }
}

/**
 *  This will execute when Previous,Next.. is clicked for pagination
 * */
function paging(pageNumber) {
       $("#myinboxform").attr(
                     "action",
                     $("#myinboxform").attr("action")
                                  + "&submit_action=agencyPagination&choosenTab=inbox&nextPage=" + pageNumber);
       document.myinboxform.submit();
}

/**
* updated in R5: This method Enable or Disable all check boxes on click on checkBox in Header
* 
 */
function selectAllCheck() {
       if (document.myinboxform.selectAll.checked == true) {
              if (document.myinboxform.check.length > 1) {
                     for ( var a = 0; a < document.myinboxform.check.length; a++) {
                           document.myinboxform.check[a].checked = true;
                           getDefaultAssignmentDetails(document.myinboxform.check[a]);
                     }
              } else {
                     document.myinboxform.check.checked = true;
                     getDefaultAssignmentDetails(document.myinboxform.check);
              }
              if (document.getElementById("reassigntouser").selectedIndex != 0) {
                     document.getElementById("reassignId").disabled = false;
              }
       } else {
              if (document.myinboxform.check.length > 1) {
                     for ( var a = 0; a < document.myinboxform.check.length; a++) {
                           document.myinboxform.check[a].checked = false;
                     }
              } else {
                     document.myinboxform.check.checked = false;
              }
              document.getElementById("reassignId").disabled = true;
       }
}

/**
* Method enable or disable filter button
* 
 */
function enableFilter() {
       if (document.getElementById("tasktype").selectedIndex == 0) {
              document.getElementById("filtersBtn").disabled = true;
       } else {
              document.getElementById("filtersBtn").disabled = false;
       }

}

/**
* Method submit the form and pass required values as parameter.
* Change done for enhancement 6534 with Release 3.8.0
*/
function submitForm(wobNum, taskName, agencyId) {
       pageGreyOut();
       $("#myinboxform").attr(
                     "action",
                     $("#myinboxform").attr("action")
                                  + "&submit_action=viewTaskDetails&choosenTab=inbox&taskType=" + taskName+"&wobNumber="+wobNum+"&agencyId="+agencyId);
       document.myinboxform.submit();
}
/**
* Check if current Task is manager task and user is manager or not.If user is not manager display error message and disable Reassign button else submit the form.
    Updated Method in R4 
**/
function ressignCall(){
       ///Release 5 
       pageGreyOut();       
       var askFlag = false;
    if( $("#check:checked").attr("financialtask") == 'true' && $("#check:checked").attr("tasktype") != 'Procurement Certification of Funds')
    {
           hideDontAskFlag = false;
           var total = 0, yesFlag = 0;
           $("#check:checked").each(function(){
                 total++;
                 if(askFlagsOfChecked[$(this).val()] != 'Y'){
                        yesFlag++;
                 }
           });
           if(total == yesFlag){
                 askFlag = true;
           }else if(yesFlag > 0){
                askFlag = true;
                 hideDontAskFlag = true;
           }
    }
    if(askFlag)
    {
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
              $('#assignTo').text($('#reassigntouser option:selected').text());
              $('#taskType').text($("#check:checked").attr("tasktype"));
              $('#taskLevel').text("Level "+$("#check:checked").attr("tasklevel"));
              $('#assigneeUserId').val($('#reassigntouser option:selected').val());
              $('#assigneeTaskType').val($("#check:checked").attr("tasktype"));
              $('#assigneeTaskLevel').val("Level "+$("#check:checked").attr("tasklevel"));
              $(".overlay").launchOverlayNoClose($(".alert-box-getDefaultAssignee"),
                           "600px", null, "onReady");
              if(hideDontAskFlag){
            $("#dontAskSpan").hide();
             }
             else
            {
                  $("#dontAskSpan").show();
            }
             $("#hiddenAskFlagOverlay").val(hideDontAskFlag);
             
              $(".exit-panel").click(function() {
                     clearAndCloseOverLay();
                     $('.checkDefaultTask').attr("checked", false);
                     $('#restoreButton').attr('disabled','disabled');
              });
              removePageGreyOut();
              }
       else
              {
              var selectvalue = document.getElementById("reassigntouser").value;
              document.myinboxform.action=document.myinboxform.action+'&submit_action=assignAgencyTask&choosenTab=inbox';
              document.getElementById('reassigntouserText').value=$('#reassigntouser option:selected').html();
              document.myinboxform.submit();
              }
}
/**
* Check if current Task is manager task and user is manager or not.If user is not manager display error message and disable Reassign button else submit the form.
**/
function ressignCallConfirm()
{
		//Emergency release changes done for default assignment
		$("#restoreButton").attr("disabled", "disabled");
       var selectvalue = document.getElementById("reassigntouser").value;
          document.myinboxform.action=document.myinboxform.action+'&submit_action=assignAgencyTask&choosenTab=inbox&fromFinancials=true';
       document.getElementById('reassigntouserText').value=$('#reassigntouser option:selected').html();
    document.myinboxform.submit();
}
// This function is called to clear and close overlay.
function clearAndCloseOverLay() {
       $("#getUserAssigneeList").html("");
       $(".overlay").closeOverlay();
       $(".overlay").hide();
}
/**
* It shows message on page.
**/              
function showMe (it, box) {
       if(box.id=='box'){
              vis = "none";
       }else{
              vis = "block";
       }
       document.getElementById(it).style.display = vis;
} 

/**
* This method Enable or Disable Reassign button on basis of selected check box
* and value in user list drop down.
* 
 */
function enableSubmit() {
       var chks = document.getElementsByName('check');
       var hasChecked = false;
       var hasCheckedAll = true;
       for ( var i = 0; i < chks.length; i++) {

              if (chks[i].checked) {
                     hasChecked = true;
              } else {
                     hasCheckedAll = false;
                     document.myinboxform.selectAll.checked = false;
              }
              taskArray[i] = chks[i].value;
       }
              if(hasChecked)
              {
                     document.getElementById("reassigntouser").disabled = false;
              }
              else
              {
                     document.getElementById("reassigntouser").disabled = true;
              }
       if (hasCheckedAll) {
              document.myinboxform.selectAll.checked = true;
       }
       if (hasChecked
                     && document.getElementById("reassigntouser").selectedIndex != 0) {
              document.getElementById("reassignId").disabled = false;
       } else {
              document.getElementById("reassignId").disabled = true;
       }
}

/**
* Method clear the selected filter values in filter
* 
 */
function clearfilter() {
       $("#tasktype option").eq(0).attr("selected", "selected");
       $("#programname option").eq(0).attr("selected", "selected");
       $("#status option").eq(0).attr("selected", "selected");
       $("#status").attr("disabled", true);
       $("#documentValuePop input[type='text']").val("");
       $("#competitionPoolTitle").attr("disabled", true);
       $("#filtersBtn").attr("disabled", true);
       $(".error").text("");
}
// This method calls commonTypeHeadCallBack passing provider name as argument     
function typeHeadCallBackProvider() {
       commonTypeHeadCallBack($('#providername').val());
}

/* This method empties the competitionPoolId and procurementId fields
Updated Method in R4*/
function callBackProcurementTitle(){
       $('#competitionPoolTitle').unbind().val("").keyup(function(e){
              if(e.keyCode!=37 && e.keyCode!=38 && e.keyCode!=39 && e.keyCode!=40)
                     replaceAllExceptAllowedChar(this);
       }).focusout(function(){
              replaceAllExceptAllowedChar(this);
       });
       $('#competitionPoolId').val("");
       $('#procurementId').val("");
}

/**
 *  This method provides the typehead search for competetion pool 
*	Updated Method in R4
**/
function callBackProcurementTitleOnSelect(){
       if($("#procurementId").val() == "P"){
              $('#competitionPoolTitle').unbind().keyup(function(e){
                     if(e.keyCode!=37 && e.keyCode!=38 && e.keyCode!=39 && e.keyCode!=40)
                           replaceAllExceptAllowedChar(this);
              }).focusout(function(){
                     replaceAllExceptAllowedChar(this);
              });
              typeHeadSearch($('#competitionPoolTitle'), $("#hiddenFetchTypeAheadNameList")
                           .val()+ "&QueryId=fetchCompetitionPoolTitleList&key=COMPETITION_POOL_ID&value=COMPETITION_POOL_TITLE&procurementTitle="+$("#procurementTitle").val(), null, null, null, "competitionPoolId");
       }else{
              $('#competitionPoolTitle').attr("disabled", true);
       }
}

/**
 * Added in R5 
*  This method is used to get Default Assignment Details.
*  */
function getDefaultAssignmentDetails(elt){
       var checkBoxValue = $(elt).val();
     if(typeof askFlagsOfChecked[checkBoxValue] == "undefined"){
         var entityId = $(elt).val().split("#")[2];
         var url = $("#getReassignListFinanceHidden").val()+"&taskLevel="+$(elt).attr("taskLevel")+"&taskType="+$(elt).attr("taskType")+"&agencyId="+$(elt).attr("agencyId")+"&entityId="+entityId+"&taskDetails="+$(elt).val();
         $.ajax({
                       url : url,
                       type : 'POST',
                       cache : false,
                       success : function(responseText, statusText, xhr ) {
                              $("#askFlag").val(responseText["message"]);
                              askFlagsOfChecked[checkBoxValue] = responseText["message"];
                              $("#reassigntouser").attr("disabled",false);
                       },
                       error : function(data, textStatus, errorThrown) {
                              removePageGreyOut();
                       }
                });
     }
}
