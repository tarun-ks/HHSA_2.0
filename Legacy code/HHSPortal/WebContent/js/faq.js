//Below function sets values to hidden id, saveServices 
function setValue(saveServices){
	$("#saveServices").val(saveServices);
}

//Below function builds url to navigate to provider faq summary
function navigateToProviderFaqSummary(userType){
		$('#providerHref').click();
}

//Below function builds url to navigate to agency faq summary
function navigateToAgencyFaqSummary(userType){
		$('#agencyHref').click();
}

//Below function submits the user form from update question/answer/topic screen to persist data to database
function formsubmit(){
    $("#topicreqd").children("span").text("");
    $("#questionreqd").children("span").text("");
    $("#answerreqd").children("span").text("");
	var lsReturnStatus = validateText('helpTopic');
	if(!lsReturnStatus && !lsRetStatusForQuesField){
    	var errorMsg1 = "! Only ! @ # $ % ^ & * () - _ + = | \ { } [ ] ; : \" \' < > , . ? / ` ~ \xA7 and \n spaces are allowed as a special character.";
    	$("#topicreqd").children("span").text(errorMsg1);
    	return false;
    }
	
	var lsRetStatusForQuesField = validateText('selectedQuestion');
	if(!lsRetStatusForQuesField){
    	var errorMsg3 = "! Only ! @ # $ % ^ & * () - _ + = | \ { } [ ] ; : \" \' < > , . ? / ` ~ \xA7 and \n spaces are allowed as a special character.";
    	$("#questionreqd").children("span").text(errorMsg3);
    	return false;
    }
    
	$("#transactionStatusDiv").hide();
	var answer1=document.myform.rte1.value;
	updateRTE('rte1');
	
    var topicname=document.getElementById("helpTopic").value;
	var question=document.getElementById("selectedQuestion").value;
	
	var lsId = document.myform.rte1.id;
	$('#rte1').contents().find('#richTextId').val(convertSpecialCharactersHTMLGlobal(lsId,true, true));
	convertSpecialCharactersHTMLGlobal(lsId,true);
	var answerText = $("#"+lsId).val();
	lsResult = "^[0-9a-zA-Z !@\\\#\\\$%^\\\&*()-_+=|\\\\{}\\\[\\\];:\\\"\\\'<>,.?\\\/`~\xA7\n\r\t]+$";
	var re = new RegExp(lsResult);
	 if(null!=answerText && answerText!=""){
 		var lsResult = re.test(answerText);
 		if(!lsResult){
 			var errorMsg2 = "Your response contains invalid characters. Please re-type your response or visit Online Help for instructions on how to Copy and Paste as plain text.";
 			 $("#answerreqd").children("span").text(errorMsg2);
     	    	return false;
     	}
	 } 	

	if(answerText.indexOf("class=MsoNormal") != -1){
			var answer = answerText.replace("class=MsoNormal","");
	}else{
			var answer=answer1;
	}
	
	var selectedCombo = document.getElementById("qusList").value;
    $("#topicreqd").children("span").text("");
    $("#questionreqd").children("span").text("");
    $("#answerreqd").children("span").text("");
	if (isEmpty(topicname)== true || trim(topicname)=="" ){
	    var errorMsg = "! This Field is Required";
	    $("#topicreqd").children("span").text(errorMsg);
   		return false;
	}else if(selectedCombo!="onlyTopicUpdate" &&(isEmpty(question)== true ||trim(question)=="")){
	    var errorMsg = "! This Field is Required";
	    $("#questionreqd").children("span").text(errorMsg);
	    return false;
	}else if(selectedCombo!="onlyTopicUpdate" &&(isEmpty(answerText)== true  || trim(answerText)=="")){
	    var errorMsg = "! This Field is Required";
	    $("#answerreqd").children("span").text(errorMsg);
	    return false;
	}
	else{ 
		//start
		if(isEmpty(answerText)== false && trim(answerText)!=""){
		var contentWithNoTag = stripHTML(document.myform.rte1.value);
		var div = document.createElement('div'); 
		div.innerHTML = contentWithNoTag; 
		var decoded = div.firstChild.nodeValue; 
		if(decoded.length > 1000){
			var errorMsg = "! Your answer should not be more than 1000 characters";
			$("#answerreqd").children("span").text(errorMsg);
			return false;
		}else if(document.myform.rte1.value.length > 2000){
			var errorMsg = "! HTML content for your answer is exceeding 2000 characters";
			$("#answerreqd").children("span").text(errorMsg);
			return false;
		}
	}
	 	var selectedValue = document.getElementById("qusList").value;
		if(selectedValue==""){
	 		selectedValue = "";
		}else if(selectedValue!="onlyTopicUpdate") {
			var arr = selectedValue.split("-");
	 		selectedValue = arr[0];
		}
		  var selAnswerValue=document.myform.rte1.value;
		  if(selAnswerValue.indexOf("class=MsoNormal") != -1){
			  var selAnswerValueWothOutClass = selAnswerValue.replace("class=MsoNormal","");
		  }else{
			  var selAnswerValueWothOutClass=selAnswerValue;
		  }
		  document.getElementById("selectedAnswer").value = selAnswerValueWothOutClass;
		  document.myform.action=action+'&next_action=formsubmit&selectedValue='+selectedValue;
		  document.myform.submit();
	}
 } 

//Below function returns true if topic name is empty/blank
function isEmpty(topicname){
    return (!topicname || 0 === topicname.length);
}  

//Below function processed the delete request for help topic
function deleteRequest(){
	var pageW = $(document).width();
	var pageH = $(document).height();
	$("#DeleteConfirmationTitle").html("Topic Deletion Confirmation");
	$("#ConfirmMessagediv").html("Are you sure you want to delete this topic?");
	$(".alert-box-delete").show();
	$(".overlay").show();
	$(".overlay").width(pageW);
	$(".overlay").height(pageH);	
	document.myform.action=action+'&next_action=deleteRequest';
}

//Below function sets the url on onload
function onload(url1){
	url=url1;
}

//Below function build url(add parameter) while user click on return button
function onReturn(){
	document.myform.action=action+'&next_action=return';
	document.myform.submit();
}

//Below function processes the delete question request(along with answer) from update page
function deletesQuestion(){
	var selectedValue = document.getElementById("qusList").value;
	if(selectedValue==""){
		selectedValue = "";
	}
	else {
		var arr = selectedValue.split("-");
		selectedValue = arr[0];
	}
	document.myform.action=action+'&next_action=deleteQuestion&selectedValue='+selectedValue;
	var pageW = $(document).width();
	var pageH = $(document).height();
	$("#DeleteConfirmationTitle").html("Question Deletion Confirmation");
	$("#ConfirmMessagediv").html("Are you sure you want to delete this question?");
	$(".alert-box-delete").show();
	$(".overlay").show();
	$(".overlay").width(pageW);
	$(".overlay").height(pageH);	
}

//Below function returns the trim value of any string (spaces removed from left and right of string, if any)
function trim(stringToTrim) {
	return stringToTrim.replace(/^\s+|\s+$/g,"");
}

//Below function removes the status div once the user proceed with other request
function proceed(){
	$(".alert-box-delete").hide();
	$(".overlay").hide();
	document.myform.submit();
}

//Below function fill the question into dropdown and gets the selected value 
function fillQuestions(){
	setChangeFlag();
	var richTextField = document.getElementById('rte1').contentWindow.document.getElementsByTagName('body')[0];
	if (navigator.userAgent.indexOf("MSIE")==-1) 
	   { // FF
		richTextField =  document.getElementById('rte1').contentDocument.getElementsByTagName('body')[0];
	   }
	   else 
	   { // IE
		   richTextField =  document.getElementById('rte1').contentWindow.document.getElementsByTagName('body')[0];
	   }
	if(document.getElementById("qusList").value!="" && document.getElementById("qusList").value!="onlyTopicUpdate"){
		var answer = document.getElementById("qusList").value;
		answer= answer.substr((answer.indexOf("-")+1));
		var quesId = document.getElementById("qusList");
		var question = quesId.options[quesId.selectedIndex].text;
		document.getElementById("selectedQuestion").value=question;
		richTextField.innerHTML=answer;
		$("#deletebutton").show();
		
	}else {
		document.getElementById("selectedQuestion").value="";
		richTextField.innerHTML="";
		$("#deletebutton").hide();
	}
	if(document.getElementById("qusList").value=="onlyTopicUpdate"){
		document.getElementById("selectedQuestion").disabled=true;
		disableLink = true;
			if (navigator.userAgent.indexOf("MSIE")==-1) 
			   { // FF
				richTextField.style.display="none";
			   }
			   else 
			   { // IE
				   richTextField.disabled=true;
			   }
	}else{
		document.getElementById("selectedQuestion").disabled=false;
		 disableLink = false;
			if (navigator.userAgent.indexOf("MSIE")==-1 ) 
			   { // FF
				richTextField.style.display="block";
			   }
			   else 
			   { // IE
				   richTextField.disabled=false;
			   }
	}
}

//This function set the boolean flag if any field change take place
function setChangeFlag(){
	isFieldchange = true;
}

//This function displays the confirmation popup for data loss
function displayAlertPopUp(){
	if(isFieldchange){
	 	displayConfirmation();
	}else {
	    returnToMain();
	}
}

//This function displays the confirmation popup for data loss
function displayConfirmation (){
    var pageW = $(document).width();
    var pageH = $(document).height();
	$("#UnsaveConfirmationTitle").html("Unsaved Data");
	$("#UnsaveMessagediv").html("You have unsaved data. If you would like to leave this screen without saving your data, click OK. If you would like to save your data, click Cancel and save your data.");
	$(".faq-unsaved-box").show();
	$(".overlay").show();
	$(".overlay").width(pageW);
    $(".overlay").height(pageH);
}

//Below function brings user to main page of faq maintenance
function returnToMain(){
    $(".faq-unsaved-box").hide();
    var url =contextPathVariable+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_maintenancemanage&_nfls=false&removeNavigator=true&navigatefrom=landing";
    location.href= url;

}

function validateText(id) {
	convertSpecialCharactersHTMLGlobal(id,true);
	var lsDescription = document.getElementById(id).value;
	lsResult = "^[0-9a-zA-Z !@\\\#\\\$%^\\\&*()-_+=|\\\\{}\\\[\\\];:\\\"\\\'<>,.?\\\/`~\xA7]+$";
	var re = new RegExp(lsResult);
	if(null!=lsDescription && lsDescription!=""){
		return re.test(lsDescription);
	}else{
		return true;
	}
}
