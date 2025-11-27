/**
 * =====================================================
 * This file handle the event on Returned Payment Review task Screen.
 * It is added in Release 6.
 * */
/**
 * The function will validate the task screen on clicking Finish button.
 */
function finishTaskValidationForReturnedPayment(){		
			var returnVal = true;
			var publicCommentVal = "";
			var internalCommentVal = "";
			
			var checkDateErrorMsg = "Check Date is invalid!";
			var receivedDateErrorMsg = "Received Date is invalid!";
			
			var taskStatus = $("#finishtaskchild").val();
			var checkNumber =$("#checkNumber").val();
			var checkAmount =$("#checkAmount").val();
			var checkDate =$("#checkDate").val();
			var receivedDate =$("#receivedDate").val();
			var descriptionInput =$("#descriptionInput").val();
			
			/* QC 9714 */
			if(!isFourDigitYear(checkDate)){				
				document.getElementById("checkDate").style.color = "red";				
				$("#taskErrorDiv").html(checkDate + ' - ' + checkDateErrorMsg);
				$("#taskErrorDiv").show();
				console.log(checkDate + ' - check date is invalid!');
				returnVal = false;				
			}			
			if(!isFourDigitYear(receivedDate)){				
				document.getElementById("receivedDate").style.color = "red";
				$("#taskErrorDiv").html(receivedDate + ' - ' + receivedDateErrorMsg);
				$("#taskErrorDiv").show();
				console.log(receivedDate + ' - received date is invalid!');
				returnVal = false;				
			}
			/* end of QC 9714 */
			
			if (document.getElementById("internalCommentArea") != null) {
				internalCommentVal = trim(document.getElementById("internalCommentArea").value);
			}
			if (document.getElementById("publicCommentArea") != null) {
				publicCommentVal = trim(document.getElementById("publicCommentArea").value);
			}
			if (taskLevel == 1 && publicCommentVal == "" && taskStatus == "Returned for Revision") 
			{
				$("#taskErrorDiv").html(publicCommentErrorMsg);
				$("#taskErrorDiv").show();
				returnVal = false;
			}
			else if (taskLevel > 1 && internalCommentVal == "" && taskStatus == "Returned for Revision") 
			{
				$("#taskErrorDiv").html(internalCommentErrorMsg);
				$("#taskErrorDiv").show();
				returnVal = false;
			}
			if(taskStatus == "Approved")
			{
			if(checkNumber == "")
			{
				$("#checkNumberErrorSpan").html("! This field is required");
				$("#checkNumberErrorSpan").show();
				returnVal = false;
			}
			if(checkAmount== "")
			{
				$("#checkAmountErrorSpan").html("! This field is required");
				$("#checkAmountErrorSpan").show();
				returnVal = false;
			}
			if(checkDate == "")
			{
				$("#checkDateErrorSpan").html("! This field is required");
				$("#checkDateErrorSpan").show();
				returnVal = false;
			}
			else{
				var month  = checkDate.substring(0,2);
				var date = checkDate.substring(3,5);
				var year  = checkDate.substring(6,10);
				var formattedReceivedDate = new Date(year,month-1,date);
				var minDate = new Date(1800,01,01);
				var today = new Date();
				month  = today.getMonth();
				date = today.getDate();
				year  = today.getFullYear();
				var maxDate =  new Date(year+50,month,date);
				if(minDate > formattedReceivedDate || formattedReceivedDate  > maxDate)
					{
					$("#checkDateErrorSpan").html("! Please enter a year equal to or after 1800");
					$("#checkDateErrorSpan").show();
					returnVal = false;
					}
			}
			if(receivedDate=="")
			{
				$("#receivedDateErrorSpan").html("! This field is required");
				$("#receivedDateErrorSpan").show();
				returnVal = false;
			}
			else{
				var month  = receivedDate.substring(0,2);
				var date = receivedDate.substring(3,5);
				var year  = receivedDate.substring(6,10);
				var formattedReceivedDate = new Date(year,month-1,date);
				var minDate = new Date(1800,01,01);
				var today = new Date();
				month  = today.getMonth();
				date = today.getDate();
				year  = today.getFullYear();
				today = new Date(year,month,date);
				if (formattedReceivedDate>today){
					$("#receivedDateErrorSpan").html("! Date cannot be in the future");
					$("#receivedDateErrorSpan").show();
					returnVal = false;
				}
				else if(minDate > formattedReceivedDate)
					{
					$("#receivedDateErrorSpan").html("! Please enter a year equal to or after 1800");
					$("#receivedDateErrorSpan").show();
					returnVal = false;
					}
			}
			if(descriptionInput=="")
			{
				$("#descriptionInputErrorSpan").html("! This field is required");
				$("#descriptionInputErrorSpan").show();
				returnVal = false;
			}
			}
			return returnVal;
		}

/* QC9714, 2023-05-16 */
function isFourDigitYear(yr){	
	
	if(!yr){
		return false;
	}
	
	if(yr == ''){
		return false;
	}
	
	const myArray = yr.split("/");
	if(myArray.length < 3){
		return false;
	}
	
	if(parseFloat(myArray[2]) < 1800){
		return false;
	}
	
	return true;
	
}
/* end of QC9714 */

/**
 *  This function is used to restrict entry of any
 *  character other than numerical characters
 **/
$.fn.validateOnlyNumber = function(){
	this.keypress(function(event) {
		if (event.which != 8 && event.which != 0 && (event.which < 48 || event.which > 57)) {
	               return false;
	    }
	});
}; 

