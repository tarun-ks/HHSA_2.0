/**
 * This method called when page is getting loaded and set the values
 **/
$(document).ready(function() {
                $("#customQuestion").find('input[type="checkbox"]').each(function(i) {
                                if ($(this).attr("checked") == undefined) {
                                                var inputObj = $("#customQuestion").find('input[type="text"]')[i];
                                                $(inputObj).attr("readonly", "true").addClass("readonly");
                                }
                });
                $("#backToFinancials").backNextButton("backToFinancials");
                
});
/**
 * function used to enable the text box
 **/
function enableTextBox(obj, counter) {
                var inputObj = $("#questionText" + counter);
                if ($(obj).attr("checked")) {
                                $(inputObj).removeClass("readonly");
                                $(inputObj).attr('readonly', false).addClass("readonlyDisabled");
                                $(inputObj).prop('readonly', false);
                                $(obj).attr("value","1");
                } else {
                                $(inputObj).attr("readonly", "true").addClass("readonly").removeClass("readonlyDisabled");
                                $("#questionText"+counter+"").val('');
                                $("#questionFlagError"+counter+"").html("");
                                $(obj).attr("value","0");
                }
}
/**
* on click of save button, if no error exist then submit the page 
 * Method modified for Release 3.6.0 enhancement #6485
*/
function saveProposalConfiguration() {
    var tmpIsError = validateQuestionText();
    var tmpCustomLabelReqError = validateCustomLabelRequiredText();
    var tmpCustomLabelOptError = validateCustomLabelOptionalText();
    if(tmpIsError || tmpCustomLabelReqError || tmpCustomLabelOptError){
                return false;
    }
    else{
                pageGreyOut();
                /*setting the action attribute*/
                $("#proposalconfigform").attr("action", $("#proposalconfigform").attr("action")
                                + "&submit_action=saveProposalConfiguration&buttonAction=save");
                /*submitting the page.*/
                 $("#proposalconfigform").submit();
    }
}
/**
* This method is validating the question text if check box is
* checked then it must contain some valu otherwise it will display the error message
* on page 
 * @returns {Boolean}
*/
function validateQuestionText(){
                var tmpIsError = false;
    var i;
    for(i=0;i<=4;i++) {
                  if($("#questionFlag"+i+"").attr('checked')) {
                                  if($("#questionText"+i+"").val().trim()=="") {
                                                  $("#questionFlagError"+i+"").html("! This field is required.");
                                                  tmpIsError = true; 
                                                }
                                }          
    }
    return tmpIsError;
}

/**
* The method validates the required custom label
* Method added for Release 3.6.0 enhancement #6485
*/
function validateCustomLabelRequiredText(){
                
                var tmpCustomLabelReqError = false;
    var i;
    for(i=0;i<=14;i++) {
                var customLabel = document.getElementById("requiredDocumentList"+i+".customLabelName").value;
                $("#customLabelReqError"+i+"").html("");
                    if(!($("#customLabelDivRequired"+i+"").is(':hidden'))){
                                                  if(customLabel.trim()=="") {
                                                                  $("#customLabelReqError"+i+"").html("! This field is required.");
                                                                tmpCustomLabelReqError = true; 
                                                                }
                                                  else if("" != customLabel.trim() && customLabel.trim().length < 5){
                                                                  $("#customLabelReqError"+i+"").html("! The Document Name must be 5 or more characters");
                                                                                                tmpCustomLabelReqError = true;  
                                                                                  }
                    }
    }
                                  return tmpCustomLabelReqError;
}

/**
* The method validates the optional custom label
* Method added for Release 3.6.0 enhancement #6485
*/
function validateCustomLabelOptionalText(){
                var tmpCustomLabelOptError = false;
    var i;
    for(i=0;i<=4;i++) {
                var customLabel = document.getElementById("optionalDocumentList"+i+".customLabelName").value;
                $("#customLabelOptError"+i+"").html("");
                if(!($("#customLabelDivOptional"+i+"").is(':hidden'))){
                if(customLabel.trim()=="") {
                                                  $("#customLabelOptError"+i+"").html("! This field is required.");
                                                tmpCustomLabelOptError = true; 
                                                }
                                  else if("" != customLabel.trim() && customLabel.trim().length < 5){
                                                                                $("#customLabelOptError"+i+"").html("! The Document Name must be 5 or more characters");
                                                                                tmpCustomLabelOptError = true;  
                                                                  }
                }
    }
    return tmpCustomLabelOptError;
}


/**
* The method called on select of Required Documents Drop down
* Method added for Release 3.6.0 enhancement #6485
*/
/**Fix for 8229 */
function checkOthersDocType(selectedDocType, i){
                document.getElementById("requiredDocumentList"+i+".customLabelName").value = "";
               if(selectedDocType == 'Solicitation - Other'){
                                $("#customLabelDivRequired"+i+"").show();
                                $("#customLabelReqError"+i+"").html("");
                }
                else{
                                $("#customLabelDivRequired"+i+"").hide();
                }
}

/**
* The method called on select of Optional Documents Drop down
* Method added for Release 3.6.0 enhancement #6485
*/
/**Fix for 8229 */
function checkOthersOptionalDocType(selectedDocType, i){
			document.getElementById("optionalDocumentList"+i+".customLabelName").value = "";
                if(selectedDocType == 'Solicitation - Other'){
                                $("#customLabelDivOptional"+i+"").show();
                                $("#customLabelOptError"+i+"").html("");
                }
                else{
                                $("#customLabelDivOptional"+i+"").hide();
                }
}
