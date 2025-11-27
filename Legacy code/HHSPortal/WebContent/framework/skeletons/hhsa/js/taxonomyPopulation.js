/**
 * This js execute for the population inside the business application and organization
 * 
 */
// function call on the page load to check the check box
var lastData = null;
$(function(){
	if($("#saveAndNextButtonId").size()>0){
		$("#buttonName").html("Save and Next");
	}else{
		$("#buttonName").html("Save");
	}
	var $form = $("#Populations").closest('form');
	lastData = $form.serializeArray();
	if($("#noPopulation").attr("checked")){
	  var boxList = $('input:checkbox');
      boxList.each(function(i){
            var checkBoxId = $(this).attr("id");
            if(checkBoxId!="noPopulation"){
                     this.checked = false;
                     this.disabled=true;
               }
         });
     }
});
//This method set the max length.
//updated in R5
function setMaxLength(obj,maxlimit){
	if(($(obj).val().length + $(obj).val().split("\n").length-1)  > maxlimit){
		$(obj).val($(obj).val().substring(0, maxlimit - $(obj).val().split("\n").length + 1));
		return false;
	}
}

/**
 * This function is called when user click on the save and save next button
 * 
 * @param next_action next action which coming from jsp
 * @param businessAppId business application id
 * @param section section 
 * @param subSection subsection 
 * @returns {Boolean} true false
 */
 function selectAllAndSubmit(next_action,businessAppId,section,subSection) {
 var isValid = true;
 		var boxList = $('input:checkbox:checked');
		if (boxList.length == 0) {
   			$("#errorMessage").html("You must select at least one Population");
   			isValid = false;
   			$("input[id^=ageRangeFrom_]").each(function(i){
   				$(this).val("");
   			});
   			$("input[id^=ageRangeTo_]").each(function(i){
   				$(this).val("");
   			});
   			return false;
		}
		if($("#otherCheckBox").attr("checked")){
			if($.trim($("#otherTextBox").val())==''){
				$("#otherTextBox").next().remove();
				$("#otherTextBox").after("<span class='individualError floatLft' style='width:34%;padding-left: 1%' id='displayErrorForNumberOther'>! This field is required</span>");
				isValid = false;
			}else{
				convertSpecialCharactersHTMLGlobal('otherTextBox',true);
				lsResult =  "^[0-9a-zA-Z !@\\\#\\\$%^\\\&*()-_+=|\\\\{}\\\[\\\];:\\\"\\\'<>,.?\\\/`~\xA7\n\r\t]+$";
				var re = new RegExp(lsResult);
		        if(!re.test($("#otherTextBox").val())){
					$("#otherTextBox").next().remove();
					$("#otherTextBox").after("<span class='individualError floatLft' style='width:34%;padding-left: 1%' id='displayErrorForNumberOther'>! Your response contains invalid characters. Please re-type your response or visit Online Help for instructions on how to Copy and Paste as plain text.</span>");
					isValid = false;
				}else{
					$("#displayErrorForNumberOther").remove();
				}
			}
		}
		$("input[id^=validateAgeRange_]").each(function(i){
			if($(this).attr("checked")){
				$(this).next().find("span").remove();
			}
		});
		$("input[id^=validateAgeRange_]").each(function(i){
			if($(this).attr("checked")){
				var ageFrom = $(this).next().find("input")[0];
				var ageTo = $(this).next().find("input")[1];
				$(ageTo).next().remove();
				if(isNaN($.trim($(ageFrom).val())) || isNaN($.trim($(ageTo).val()))){
					$(ageTo).after("<span class='individualError' id='displayErrorForNumber"+i+"'>Please enter only number</span>");
					isValid = false;
				}
				if($.trim($(ageFrom).val())=='' || $.trim($(ageTo).val())==''){
					$(ageTo).after("<span class='individualError' id='displayErrorMsg"+i+"'>! This field is required</span>");
					isValid = false;
				}
				if(parseInt($.trim($(ageFrom).val())) > parseInt($.trim($(ageTo).val()))){
					$(ageTo).after("<span class='individualError' id='errorComparision"+i+"'>! This is not a valid age range</span>");
					isValid = false;
				}
			}
		});	
		if(isValid){
			document.forms[0].action = document.forms[0].action+"&business_app_id="+businessAppId+"&next_action="+next_action+"&section="+section+"&subsection="+subSection;
			document.forms[0].submit();
		}
	}
 
/**
 * This function displays age range for the required population
 * 
 * @param obj
 * @param type
 */ 
function displayAgeRange(obj,type){
var isValild = false;
		$("input[name=populationCheckBoxex]").each(function(i){
			if($(this).attr("checked")){
				isValild = true;
			}
		});
		
		if($(obj).attr("checked")){
			$(obj).next().removeClass().addClass("popuAgeRangeBlock");
		}else{
			$(obj).next().find("span").remove();
			$(obj).next().removeClass().addClass("popuAgeRangeNone");
		}
		$("#errorMessage").html("");
}

/**
 * This function called when no other population selected checkbox
 * 
 * @param obj
 */
function disableAllPopulation(obj){
	var boxList = $('input:checkbox');
    if($(obj).attr("checked")){
           boxList.each(function(i){
                 var checkBoxId = $(this).attr("id");
                 if(checkBoxId!="noPopulation"){
                       this.checked = false;
                       this.disabled=true;
                 }
           });
           $("div[id^=allAgeRangeIds]").each(function(i){
           		$(this).removeClass().addClass("popuAgeRangeNone");
           });
           $("#otherTextBox").hide();
           $("#displayErrorForNumberOther").remove();
       }else{
       		boxList.each(function(i){
                 var checkBoxId = $(this).attr("id");
                 if(checkBoxId!="noPopulation"){
                       this.checked = false;
                       this.disabled=false;
                 }
           });
       } 
      $("#errorMessage").html("");
	}

/**
 * This function called when user click on the other check box in the population
 * 
 * @param obj
 */
function displayOtherArea(obj){
	if($(obj).attr("checked")){
			var boxList = $('input:checkbox');
            $("#otherTextBox").show();
            $(obj).removeAttr("disabled");
            $("#otherTextBox").val("");
	}else{
        $("#otherTextBox").val("");
        $("#displayErrorForNumberOther").remove();    
		$("#otherTextBox").hide();
	}
	$("#errorMessage").html("");
}

/**
 * This function called when user click on the back button
 * 
 * @param pageToDirect
 * @param business_app_id
 * @param section
 * @param subsection
 */
 function GoToPreviousPage(pageToDirect,business_app_id,section,subsection) {
	 var $self=$(this);
		var $form = $("#Populations").closest('form');
		var isSame = false;
		data = $form.serializeArray();
		if(lastData != null){
			if($(lastData).compare($(data))){
				isSame = true;
			}
		}
		if(!isSame && lastData != null){
			$('<div id="dialogBox"></div>').appendTo('body')
			.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
			.dialog({
				modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
				width: 'auto', modal: true, resizable: false, draggable:false,
				dialogClass: 'dialogButtons',
				buttons: {
					OK: function () {
						if(pageToDirect != 'refresh'){
						document.populationform.action = document.populationform.action+"&business_app_id="+business_app_id+"&next_action=back&section="+section+"&subsection="+subsection;
			    		document.populationform.submit();
						$(this).dialog("close");
					}
			else{
				location.href=$("#contextPathSession").val()+"/portal/hhsweb.portal?_nfpb=true&_st=&_windowLabel=portletInstance_21&_urlType=render&wlpportletInstance_21_next_action=open&wlpportletInstance_21_app_menu_name=header_organization_information&wlpportletInstance_21_subsection=populations&wlpportletInstance_21_action=orgBasicInformation&wlpportletInstance_21_section=basics";
			}
					},
					Cancel: function () {
						$(this).dialog("close");
					}
				},
				close: function (event, ui) {
					$(this).remove();
				}
			});
			$("div.dialogButtons div button:nth-child(2)").find("span").addClass("graybtutton");
		}else{
			if(pageToDirect != 'refresh'){
			document.populationform.action = document.populationform.action+"&business_app_id="+business_app_id+"&next_action=back&section="+section+"&subsection="+subsection;
    		document.populationform.submit();
		}
		else{
			location.href=$("#contextPathSession").val()+"/portal/hhsweb.portal?_nfpb=true&_st=&_windowLabel=portletInstance_21&_urlType=render&wlpportletInstance_21_next_action=open&wlpportletInstance_21_app_menu_name=header_organization_information&wlpportletInstance_21_subsection=populations&wlpportletInstance_21_action=orgBasicInformation&wlpportletInstance_21_section=basics";
		}
		}
		}