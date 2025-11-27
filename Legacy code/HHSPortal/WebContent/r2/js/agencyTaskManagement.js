/**
 * This file contains methods that will handle the
 * functionality of agency task Management
 */       suggestionVal = "";
       isValid = false;
       var taskArray = new Array();
       var selected = new Array();
       var onload = "";
       var selectedVal = 0;
       /*This method call on page load
       Updated Method in R4*/
       
       // changes for R5 start
       var askFlagsOfChecked = {};
       var hideDontAskFlag = false;
       // changes for R5 ends
       $(document).ready(
                     function() {
                           //Release 5
                           $(".exit-panel").click(function() {
                                  clearAndCloseOverLay();
                           });
                           // for competition pool typehead 
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
                           //for sorting
                           $("#tasktype").html($("#tasktype>option", $(this)).sort(function(a, b) { 
                               var arel = $(a).html();
                               var brel = $(b).html();
                               return arel == brel ? 0 : arel < brel ? -1 : 1; 
                           }));
                           if($("#isFinanceTask").val() == "true" || $("#isR2TaskSelectAllDisable").val() == "true"){
                                  $("#selectAll").attr("disabled", true);
                                  
                           }//enabling submit
                           // Added check to enable bulk assignment as part of 3.2.0 enhancement for bulk assignment QC: 6361
                          if($("#enableBulkAssign") != null && $("#enableBulkAssign").val() == "true")
                           {
                                  $("#selectAll").attr("disabled", false);
                            }
                           $(".taskCheckBox").click(function(){
                                  pageGreyOut();
                                  // Added check to enable bulk assignment as part of 3.2.0 enhancement for bulk assignment QC: 6361
                                  if(!$(this).is(":checked")&& $("#isFinanceTask").val() == "true" &&
                                                !($("#enableBulkAssign") != null && $("#enableBulkAssign").val() == "true"))
                                  {
                                         $("#reassigntouser").empty();
                                  }
                                  // Added check to enable bulk assignment as part of 3.2.0 enhancement for bulk assignment QC: 6361
                                  if($("#enableBulkAssign") != null && $("#enableBulkAssign").val() == "true"){
                                         $("#reassigntouser option").eq(0).attr("selected", "selected");
                                  }
                                  enableSubmit();
                                  if(!($(this).is(":checked") && $(this).attr("financialTask") == "true")){
                                	  askFlagsOfChecked = {};
                                  }
                                  //3.2.0 enhancement for bulk assignment QC: 6361
                                  if($(this).is(":checked") && $(this).attr("financialTask") == "true"){
                                         if(!($("#enableBulkAssign") != null && $("#enableBulkAssign").val() == "true"))
                                         {
                                         $(".taskCheckBox").each(function(){                                         
                                                       if(!$(this).is(":checked")){
                                                              $(this).attr("disabled", true);
                                                       }
                                                });
                                         }
                                         //Release 5
                                        getDefaultAssignmentDetails(this);
                                            removePageGreyOut();
                                                //Ending 3.2.0 enhancement for bulk assignment  QC: 6361
                                  }
                                  else if($(this).is(":checked") && $("#isR2TaskSelectAllDisable").val() == "true"){
                                         removePageGreyOut();
                                         $(".taskCheckBox").each(function(){
                                                if(!$(this).is(":checked")){
                                                       $(this).attr("disabled", true);
                                                }
                                         });
                                  
                                  }else{
                                         removePageGreyOut();
                                         $(".taskCheckBox[id!=selectAll]").attr("disabled", false);
                                  }
                           });
                           //This method filters the agency tasks
                           $("#tasktype").change(
                                         function() {
                                                pageGreyOut();
                                                var selectedValue = $(this).val();
                                                if (selectedValue != " ") {
                                                       var url = $("#getAssignedToFilterHidden").val()+"&taskType="+$(this).val();
                                                       var jqxhr = $.ajax({
                                                              url : url,
                                                              type : 'POST',
                                                              cache : false,
                                                              success : function(data) {
                                                                     removePageGreyOut();
                                                                    var statusData = $(
                                                                                  ".hiddenStatusMap[key='" + selectedValue
                                                                                                + "']").val();
                                                                     $("#status").html("<option value=''> </option>" + statusData);
                                                                     $("#status").attr("disabled", false);
                                                                     $("#assignedto").html(data);
                                                                     $("#assignedto").attr("disabled", false);
                                                                     if(onload == "onload"){
                                                                           $("#status option[value=\""+$("#dropDownValuePrevstatus").val()+"\"]").attr("selected", "selected");
                                                                           $("#assignedto option[value=\""+$("#dropDownValuePrevassignedto").val()+"\"]").attr("selected", "selected");
                                                                           onload = "";
                                                                     }
                                                              },
                                                              error : function(data, textStatus, errorThrown) {
                                                                     removePageGreyOut();
                                                              }
                                                       });
                                                } else {
                                                       $("#status").html("");
                                                       $("#status").attr("disabled", true);
                                                       $("#assignedto").html("");
                                                       $("#assignedto").attr("disabled", true);
                                                       removePageGreyOut();
                                                }
                                         });
                           typeHeadSearch($('#providername'),  $("#contextPathSession").val() + '/AutoCompleteServlet.jsp?selectedpage=inbox',null,"typeHeadCallBackProvider",null);
                           preSelectFilter();
                           removePageGreyOut();
                     });
       
       
    /**
     *    this method will preselect the values in filter
     * */
       function preSelectFilter(){
              onload = "onload";
              $("#tasktype option[value=\""+$("#dropDownValuePrevtasktype").val()+"\"]").attr("selected", "selected");
              $("#tasktype").change();
              $("#programname option[value=\""+$("#dropDownValuePrevprogramname").val()+"\"]").attr("selected", "selected");
       }
       
/**
 *  This will execute when Filter Documents tab is clicked or closed
 * */
       function setVisibility(id, visibility) {
              if ($("#" + id).is(":visible")) {
                     document.myTaskMform.reset();
              }
              $("#" + id).toggle();
              
              if($("#dropDownValuePrevtasktype").val()=="")
              {
              $("#tasktype>option").eq(0).attr('selected', 'selected');
              document.getElementById("filtersBtn").disabled = true;
              document.getElementById("status").disabled = true;
              document.getElementById("assignedto").disabled = true;
              }
       }
       /**
        *  This method called for sorting*/
       function sort(columnName) {
              document.myTaskMform.reset();
              $("#myTaskMform")
                           .attr(
                                         "action",
                                         $("#myTaskMform").attr("action")
                                                       + "&choosenTab=taskmanager&submit_action=agencySorting&sortGridName=agencyTaskManagement"
                                                       + sortConfig(columnName));
              document.myTaskMform.submit();
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
              
              var startDate = new Date($("#datefrom").val());
              var endDate = new Date($("#dateto").val());
              if (!checkStartEndDatePlanned(startDate, endDate)) {
                     $("#dateto").parent().next().html(
                                  "! End Date can not be less than Start Date.");
                     return false;
              }
       
              if ($("#providername").val().length > 0 && suggestionVal.length > 0) {
                     var isValidLocal = isAutoSuggestValid($("#providername").val(),
                                  suggestionVal);
                     if (!isValidLocal)
                           isValid = false;
              }
              
              var today = new Date();
              $('#myTaskMform input:text').each(function() {
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
              
              if (isValid) {
                     pageGreyOut();
                     document.myTaskMform.action = document.myTaskMform.action
                                  + "&filteristrue=yes&submit_action=inboxFilter&choosenTab=taskmanager";
                     document.myTaskMform.submit();
              }
       }
       
       /**
        *  This will execute when Previous,Next.. is clicked for pagination
        *  */
       function paging(pageNumber) {
              $("#myTaskMform").attr(
                           "action",
                           $("#myTaskMform").attr("action")
                                         + "&submit_action=agencyPagination&choosenTab=taskmanager&nextPage=" + pageNumber);
              document.myTaskMform.submit();
       }
       
       /**
       * This method Enable or Disable all check boxes on click on checkBox in Header
       * 
        */
       function selectAllCheck() {
              if (document.myTaskMform.selectAll.checked == true) {
                     if (document.myTaskMform.check.length > 1) {
                           for ( var a = 0; a < document.myTaskMform.check.length; a++) {
                                  document.myTaskMform.check[a].checked = true;
                                  getDefaultAssignmentDetails(document.myTaskMform.check[a]);
                           }
                     } else {
                           document.myTaskMform.check.checked = true;
                           getDefaultAssignmentDetails(document.myTaskMform.check);
                     }
                     if (document.getElementById("reassigntouser").selectedIndex != 0) {
                           document.getElementById("reassignId").disabled = false;
                     }
              } else {
                     if (document.myTaskMform.check.length > 1) {
                           for ( var a = 0; a < document.myTaskMform.check.length; a++) {
                                  document.myTaskMform.check[a].checked = false;
                           }
                     } else {
                           document.myTaskMform.check.checked = false;
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
              $("#myTaskMform").attr(
                           "action",
                           $("#myTaskMform").attr("action")
                                         + "&submit_action=viewTaskDetails&choosenTab=taskmanager&taskType=" + taskName+"&wobNumber="+wobNum+"&agencyId="+agencyId);
              document.myTaskMform.submit();
       }
       
       /**
        * This function closes the Add Assignee overlay
        * */
       function clearAndCloseOverLay() {
              $("#getUserAssigneeList").html("");
              $(".overlay").closeOverlay();
       
              //Build 3.1.0, enhancement 6020, added hide method when overlay is closed
              $(".overlay").hide();
       }
       /**
       * Check if current Task is manager task and user is manager or not.If user is not manager display error message and disable Reassign button else submit the form.
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
                     removePageGreyOut();
                     $('.checkDefaultTask').attr("checked", false);
                     $('#restoreButton').attr('disabled','disabled');
              }
              else
                     {
                        document.myTaskMform.action=document.myTaskMform.action+'&submit_action=assignAgencyTask&choosenTab=taskmanager';
                     document.getElementById('reassigntouserText').value=$('#reassigntouser option:selected').html();
                  document.myTaskMform.submit();
                     }
       }
       /**
        * This method ressign Call Confirm on basis of selected check box
        * and value in user list drop down.
        * 
         */
       function ressignCallConfirm()
       {
    		//Emergency release changes done for default assignment
   		$("#restoreButton").attr("disabled", "disabled");
              //var selectvalue = document.getElementById("reassigntouser").value;
                 document.myTaskMform.action=document.myTaskMform.action+'&submit_action=assignAgencyTask&choosenTab=taskmanager&fromFinancials=true';
              document.getElementById('reassigntouserText').value=$('#reassigntouser option:selected').html();
              $("#check:checked").each(function(){
                 $(this).val($(this).val()+"#"+askFlagsOfChecked[$(this).val()])
            });
           document.myTaskMform.submit();
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
                           document.myTaskMform.selectAll.checked = false;
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
                     document.myTaskMform.selectAll.checked = true;
              }
              if (hasChecked&& document.getElementById("reassigntouser").selectedIndex > 0) {
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
              $("#assignedto option").eq(0).attr("selected", "selected");
              $("#assignedto").attr("disabled", true);
              $("#documentValuePop input[type='text']").val("");
              $("#competitionPoolTitle").attr("disabled", true);
              $("#filtersBtn").attr("disabled", true);
              $(".error").text("");
       }
      
       /**
       *   This method calls commonTypeHeadCallBack passing providername as parameter
       *   */
       function typeHeadCallBackProvider() {
              commonTypeHeadCallBack($('#providername').val());
       }
       
/**
 *         This method empties the competitionPoolId and procurementId fields
*/ 
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
 *         This method provides for the typehead search and disabling of competetion pool field 
 *         */
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
 *  This method is called to get Default Assignment Details
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
                              $("#reassigntouser").html(responseText["message"]);
                              $("#askFlag").val(responseText["message2"]);
                              askFlagsOfChecked[checkBoxValue] = responseText["message2"];
                              $("#reassigntouser").attr("disabled",false);
                       },
                       error : function(data, textStatus, errorThrown) {
                              removePageGreyOut();
                       }
                });
     }
}
