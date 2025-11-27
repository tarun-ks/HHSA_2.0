/**
 * ===============================================================
 * This file contains the functionalities that
 * is commonly used across screen.
 * This utilities contains the function related to changing format
 * or change date type, typeHead Functionalities.
 * ===============================================================
 */
var invalidResponseMsg = "! Your response contains invalid characters. Please re-type your response or visit Online Help for instructions on how to Copy and Paste as plain text.";
/**
 * This method changes the number to comma separated form
 **/
$.fn.changeCommaNumber = function(){
	$(this).format({
		format : "#,###",
		locale : "us"
	});
};
/**
 *  This method changes the number to currencyFormat
 **/
$.fn.changeCurrency = function(){
	$(this).format( {
		format : "#,###.00",
		locale : "us"
	});
};
/**
 *  This method keeps check if char types is number
 **/
$.fn.validateNumber = function(){
	this.keypress(function(event) {
		if (validateNumber(this, event)) {
			return true;
		}
		return false;
	});
};
/**
 * This method keeps check Currency On Load
 **/
$.fn.validateCurrencyOnLoad = function(){
	$(this).format( {
		format : "#,###.00",
		locale : "us"
	});
};

/**
*  This is JQGrid's top value currency format. i.e., for positive currency $1,000.00 and for negative ($1,000.00)
*/
$.fn.jqGridCurrency = function(){
	if(typeof $(this).html() != "undefined" && $(this).html() != 'null' &&  $(this).html() != '' && $(this).html() != null){
		$(this).html(jqGridformatCurrency(new Big($.trim($(this).html()))));
	}else{
		$(this).html(jqGridformatCurrency('$0.00'));
	}
};
/**
 * This is a JQGrid written to Delimit Numbers.
 **/
function jqGridDelimitNumbers(str) {
	return (str + "").replace(/(\d+)((\.\d+)*)/g,
	function(a, b, c) {
		return (b.charAt(0) > 0&& !(c || ".").lastIndexOf(".") ? b.replace(/(\d)(?=(\d{3})+$)/g,'$1,'): b)+ (c.length > 3 ? Math.round(c * 100) / 100 : c);
	});
}
/**
 * This is JQGrid's top value currency format. i.e., for positive currency $1,000.00 and for negative ($1,000.00)
*/
function jqGridformatCurrency(cellvalue) {
	var currType = '';
	var tmp = '';
	if ($.isNumeric(cellvalue)) {
		currType = '$';
		cellvalue = jqGridDelimitNumbers(cellvalue);
		if (cellvalue.indexOf('.') !== -1) {
			var valAfterDec = cellvalue.substring(cellvalue.lastIndexOf('.') + 1);
			if (valAfterDec < 10) {
				tmp = '';
			}
			cellvalue = cellvalue + tmp;
		} else {
			cellvalue = cellvalue + '.00';
		}
	} else {
		currType = '$';
		cellvalue = '0.0';
	}
	tmp = '';
	if(cellvalue.charAt(0) == '.'){
		currType+='0';
	}
	if(cellvalue.substring(cellvalue.lastIndexOf('.') + 2) == ''){
		tmp = '0';
	}
var result = currType + cellvalue + tmp;
if(result.indexOf('-') !== -1){
		result = '(' + result.replace('-','') + ')';
}
	return result;
}

/**
 *  Method to enable and disable checkbox
 **/
function enableDisableCheckBoxFull(changedNode) {
	var grpClassLocal = groupClass;
	var otClassLocal = otherClass;
	if (changedNode.hasClass(otherClass)) {
		grpClassLocal = otherClass;
		otClassLocal = groupClass;
	}
	var groupCSSLocal = "." + grpClassLocal;
	var otherCSSLocal = "." + otClassLocal;
	var someChecked = false;
	$(groupCSSLocal).each(function() {
		if ($(this).is(":checked")) {
			someChecked = true;
		}
	});
	if (someChecked) {
		$(otherCSSLocal).attr("disabled", true).attr('checked', false);
	} else {
		$(otherCSSLocal).attr("disabled", false);
	}
}

/**
 *  function to launch overlay.
 **/
$.fn.launchOverlayNoClose = function(alertbox, width, height, callBack) {
	overlayLaunched = $(this);
	alertboxLaunched = alertbox;
	var pageW = $(document).width();
	var pageH = $(document).height();
	if (width != null && typeof (width) != "undefined") {
		alertboxLaunched.width(width);
	}
	if (height != null && typeof (height) != "undefined") {
		alertboxLaunched.height(height);
	}
	alertboxLaunched.show();
	overlayLaunched.show().width(pageW).height(pageH);
	newLaunch = true;
	$(window).resize();
	callBackInWindow(callBack);
};
/**
 * Updated for R4 - Added key checks to close typeahead suggestions as well as removed time delays.
 **/
function typeHeadSearch(inputBoxObj, url, buttonIdToEnable, typeHeadCallBack, errorSpanObj, dataObjId, callBackOnSelect) {
	// This will execute when user types any character in Add
	// Provider text box.
	if (inputBoxObj != null
			&& typeof (inputBoxObj) != "undefined" && inputBoxObj.size() > 0) {
		inputBoxObj.keyup(function(evt) {
			if (suggestionVal.length > 0 && options.minChar <= inputBoxObj.val().length)
				$(".autocomplete").show();
			var keyCode = evt ? (evt.which ? evt.which : evt.keyCode)
					: event.keyCode;
			if (buttonIdToEnable != null
					&& typeof (buttonIdToEnable) != "undefined" && keyCode != 13) {
				document.getElementById(buttonIdToEnable).disabled = true;
			}
			if (keyCode == 13) {
				evt.stopPropagation();
				return false;
			}
		});
		var onAutocompleteSelect = function(value, data) {
			if (errorSpanObj != null
					&& typeof (errorSpanObj) != "undefined") {
				errorSpanObj.html("");				
			}
			isValid = true;
			if($("#providerId").length>0){
				$("#providerId").val(data);
			}
			if (dataObjId != null
					&& typeof (dataObjId) != "undefined" && $("#"+dataObjId).length>0) {
				$("#"+dataObjId).val(data);			
			}	
			if (buttonIdToEnable != null
					&& typeof (buttonIdToEnable) != "undefined") {
				document.getElementById(buttonIdToEnable).disabled = false;
			}
			if (callBackOnSelect != null
					&& typeof (callBackOnSelect) != "undefined") {
				callBackInWindow(callBackOnSelect);
			}
		};
		
		var options = {
			serviceUrl : url,
			width : 240,
			minChars : 3,
			maxHeight : 150,
			delimiter : null,
			onSelect : onAutocompleteSelect,
			clearCache : true,
			callBackMethod : typeHeadCallBack,
			params : {
				epin : inputBoxObj.val(),
				procurementId : '10'
			}
		};
		inputBoxObj.autocomplete(options);
	}
}

/**
 * This function disable the search button in case no value is provided
 **/
function commonTypeHeadCallBack(valueToSearch) {
var isValid = isAutoSuggestValid(valueToSearch, suggestionVal);
if (!isValid && valueToSearch.length >= 3) {
	$(".autocomplete").html("").hide();
	suggestionVal = "";
} 
}

/**
 *  This will execute during type head search for provider
 **/
function isAutoSuggestValid(variableName, suggestionVal) {
	var uoValid = false;
	if (suggestionVal.length > 0) {
		for (i = 0; i < suggestionVal.length; i++) {
			var arrVal = suggestionVal[i].toUpperCase();
			if (arrVal.indexOf(variableName.toUpperCase()) > -1) {
				uoValid = true;
				break;
			}
		}
	}
	return uoValid;
}
/**
 *  This will execute during type head search for provider
 **/
function isAutoSuggestValidComplete(variableName, suggestionVal) {
	var uoValid = false;
	if (suggestionVal.length > 0) {
		for (i = 0; i < suggestionVal.length; i++) {
			var arrVal = suggestionVal[i].toUpperCase();
			if (arrVal==variableName.toUpperCase()) {
				uoValid = true;
				break;
			}
		}
	}
	return uoValid;
}
/**
 *  Loading on div close
 **/
$.fn.loadingHomeClose = function () {
	var overlayId = "overlay_"+$(this).attr("id");
	if($.inArray(overlayId, loadingBars)>=0){
		$("#"+overlayId).fadeOut();
		loadingBars.splice($.inArray(overlayId, loadingBars), 1);
	}
};
/**
 *  Loading on div open
 **/
$.fn.loadingHome = function (innerText) {
	var overlayId = "overlay_"+$(this).attr("id");
	var imageloadId = "imageLoad_"+$(this).attr("id");
	if($.inArray(overlayId, loadingBars)<0){
		loadingBars.push(overlayId);
		var $t = $(this);
		if($("#"+overlayId).size()==0 || ($t.outerWidth() != $("#"+overlayId).width() || $t.outerHeight() != $("#"+overlayId).height())){
			if($("#"+overlayId).size()>0){
				$("#"+overlayId).remove();
			}
			var toInclude = '<div id='+overlayId+' class="overlayClass"><div class="img-load" id="'+imageloadId+'"><div><img src="../framework/skins/hhsa/images/loadingBlue.gif" /></div><div>'+innerText+'</div></div></div>';
			$(toInclude).insertAfter($(this));
			$(this).parent().css({
				position : "relative"
			});
			$("#"+overlayId).css({
				position : "absolute",
			  opacity : 0.8,
			  top     : 0,
			  left	: 0,
			  width   : $t.outerWidth(),
			  height  : $t.outerHeight()
			});
			$("#"+imageloadId).css({
				  top  : (($t.height() - $("#"+imageloadId).height())/ 2),
				  left : (($t.width() - $("#"+imageloadId).width())/ 2)
			});
		}
		$("#"+overlayId).fadeIn();
	}
};


/**
 *  Method for rendering a page.
 **/
function hhsAjaxRender(/* object */event, /* object */p_form, /* jsonObj */options, url, callbackMethodName) {
		var hhsAjaxMode = null; // initialize ajax mode to 'null'
		// get the clicked button's name
		var submitName = p_form.id;
		var linkObj = p_form;

		// get the clicked button's form id
		var formID;
		var forms = $(linkObj).closest("form");
		if (forms.length > 0) {
			formID = forms[0].id;
		} 
		var jQueryableFormID = formID.replace(':', '\\:');
		var cform = document.getElementById(formID);

		var element = getClickedObj(submitName, cform);
		
		// element is nothing but a complete button id object that is clicked
		if (!element) {
			element = linkObj;
		}
		// this is nothing but a URL of which is alreay in Navigation bar
		var formActionUrl = cform.action;

		var ajaxUrl = url;
		var serializedForm = null;

		var reRenderIdArray = options;
			
		// this is nothing but a hidden field of type submit
		var createdTmpHidden = createAjaxInput(element);
	
		var formObj = $('#' + jQueryableFormID);
		formObj.append(createdTmpHidden);

		try {
			serializedForm = formObj.serialize();
		} catch (e) {
		
		}	
		if (BrowserDetect.browser != 'Explorer'){
			cform.removeChild(createdTmpHidden);
		}
		
		var callbackParams = {
			jQueryableFormID : jQueryableFormID,
			formID : formID,
			reRenderIdArray : reRenderIdArray,
			options : options,
			submitName : submitName
		};

		var jqxhr = $.ajax( {
			url : ajaxUrl,
			type : 'POST',
			data : serializedForm,
			cache : false,
			beforeSend : function() {
			},
			success : function(data) {
				hhsAjaxCallback(data, hhsAjaxMode, callbackParams);
				callBackInWindow(callbackMethodName);
			},
			error : function(data, textStatus, errorThrown) {
			},
			complete : function() {
			//Added for selectVault in R5
				var $tableObj = $('#reRenderId').find('table');
				if(typeof $tableObj != 'undefined'){
					$tableObj.css('width','100%');			
				}
				//Endfor selectVault in R5
				$.unblockUI();
			}
		});
	}
/**
 * * retrieve clicked object
 */
function getClickedObj(/* string */clickedObjName, /* object */cform) {
	var elemArray = cform.elements;
	for ( var i = 0; i < elemArray.length; i++) {
		var clickedObj = elemArray[i];
		var elemType = clickedObj.type.toUpperCase();
		var elemName = clickedObj.name;
		if (elemType == "SUBMIT" || elemType == "BUTTON" || elemType == "IMAGE") {
			if (elemName == clickedObjName) {
				return clickedObj;
			}
		}
	}
}
/**
 *  Method to create ajax input.
 **/
function createAjaxInput(/* object */element) {
	var ajaxActionInput = null;
	ajaxActionInput = document.createElement("input");
	ajaxActionInput.type = "hidden";

	if (element.tagName.toUpperCase() == 'A') {
		ajaxActionInput.name = element.id;
		ajaxActionInput.value = element.text;
	} else if (element.tagName.toUpperCase() == 'DIV') {
		ajaxActionInput.name = element.id;
		ajaxActionInput.value = element.text;
	} else if ((element.tagName.toUpperCase() == 'INPUT')
			&& ($(element).attr('type').toUpperCase() == 'SUBMIT')) {
		ajaxActionInput.name = element.id;
		ajaxActionInput.value = element.value;
	} else if ((element.tagName.toUpperCase() == 'INPUT')
			&& (!$(element).is(':checked'))) {
		ajaxActionInput.name = element.id;
	} else {
		ajaxActionInput.name = element.name;
		ajaxActionInput.value = element.value;
	}
	ajaxActionInput.id = element.id;
	return ajaxActionInput;
};
/**
 *  Method to refersh element.
 **/
function refreshElementById(id, responseDOM, jQueryableFormId) {
	var idToRefresh = jQueryableFormId;
	var foundId = id;
	if (foundId != null)
		idToRefresh = foundId;
	var objToRefresh = $(document).find('#' + idToRefresh);

	var objRefreshed = responseDOM.find('#' + idToRefresh);
	
	if((objToRefresh.length == 0) || (objRefreshed.length == 0)){
		return;
	}
	try{
		objToRefresh.html(objRefreshed.html());
	} catch(e){
		var idToRefreshJavascript = idToRefresh.replace(/\\:/g,':');
		document.getElementById(idToRefreshJavascript).innerHTML = objRefreshed.html();
	}
};

/**
 *  Method hhs Ajax Call back.
 **/
function hhsAjaxCallback(responseData, mode, params) {
	var jQueryableFormID = params.jQueryableFormID;
	var formID = params.formID;
	var reRenderIdArray = params.reRenderIdArray;
	var options = params.options;
	var submitName = params.submitName;

	// modify DOM
	var loadDOM = $(responseData);

	if (reRenderIdArray != null && reRenderIdArray != "") {
		var splitArray = reRenderIdArray.split(",");
		for(var i=0;i<splitArray.length;i++){
			var reRenderIdObj = splitArray[i];
			if (reRenderIdObj != "") {
				refreshElementById(reRenderIdObj,loadDOM,jQueryableFormID);
			}
		}	
	}
};

/**
 *  sort configuration method
 **/
function sortConfig(columnName){
	var prevColumn = $("#sortBy").val();
	var sortType = $("#sortType").val();
	if (columnName == prevColumn) {
		if (sortType.toLowerCase() == "asc") {
			sortType = "desc";
		} else {
			sortType = "asc";
		}
	} else {
		sortType = "default";
	}
	return "&columnName=" + columnName + "&sortType=" + sortType;
}
/**
 *  This method is called to perform on load validations
 **/
function putOnloadValidation(){
	$("input[type='text']").each(function(){
		if($(this).attr("validate")=='currency'){
			$(this).validateCurrency();
		}else if($(this).attr("validate")=='number'){
			$(this).keypress(function(event) {
				return isNumber(this, event);
			});
		}else if($(this).attr("validate")=='alphaNumeric'){
			$(this).keypress(function(event) {
				return fnAllowAlphaNumerics(event);
			});
		}else if($(this).attr("validate")=='calender'){
			$(this).keypress(function(event) {
				event = (event) ? event : window.event;
				var charCode = (event.which) ? event.which : event.keyCode;
				var isValid = isNumber(this,event);
	             if(isValid){
	            	 if(charCode==8 || charCode==46){
	            		return true; 
	            	 }else{
	            		 validateDate(this,event);
	            	 }
	             }else{
	                 return false;
	             }
			});
			$(this).keyup(function(event) {
				event = (event) ? event : window.event;
				var charCode = (event.which) ? event.which : event.keyCode;
				if($(this).val().length == 2 || $(this).val().length == 5) {
					if(charCode==8 || charCode==46){
						return true;
					}else{
						$(this).val($(this).val() + '/');
					}
				}
			});
			$(this).blur(function(event) {
				verifyDate(this);
			});
		}
	});
	$("input, textarea").focus(function(){
		if($(this).is('[readonly]') || $(this).is(':disabled')){
			$(this).blur();
		}
	});
	$("a[href='#']").attr("href", "javascript:");
}

/**
 *  function to remove a particular parameter from url
 **/
function removeParamFromURL(url, parameter){
	var urlparts= url.split('?');  
	if (urlparts.length>=2) {
	    var prefix= encodeURIComponent(parameter)+'=';
	    var pars= urlparts[1].split(/[&;]/g);
	    for (var i= pars.length; i-->0;){
	        if (pars[i].lastIndexOf(prefix, 0)!==-1){
	            pars.splice(i, 1);
	        }
	    }
	    url= urlparts[0]+'?'+pars.join('&');
	}
	return url;
}


/**
 * jquery validation method to validate if multiselect has more than certain elements
 **/
jQuery.validator.addMethod("multiSelectHas", function(value, element, arg) {
	return  $(element).find("option").size()>=arg;
});

/**
 *  jquery validation method to validate date format
 **/
jQuery.validator.addMethod("DateFormat", function(value, element) {
	var date_regex = /^(0[1-9]|1[0-2])\/(0[1-9]|1\d|2\d|3[01])\/\d{4}$/ ;
	var comp = value.split('/');
	var m = parseInt(comp[0], 10);
	var d = parseInt(comp[1], 10);
	var y = parseInt(comp[2], 10);
	// Start Added in R5
	if(y<100)
		y = 1900+y;
	// End Added in R5
	var date = new Date(y,m-1,d);
	return this.optional(element) || (comp.length == 3 && (date_regex.test(value) && date.getFullYear() == y && date.getMonth() == m - 1 && date.getDate() == d));
});
/**
 * jquery validation method to validate date range
 **/
jQuery.validator.addMethod("DateToFrom", function(value, element, arg) {
	var arg0 = arg[0];
	var arg1 = arg[1];
	var checkFlag = arg[2];
	var comp = value.split('/');
	var m = parseInt(comp[0], 10);
	var d = parseInt(comp[1], 10);
	var y = parseInt(comp[2], 10);
	var currentEltdate = new Date(y,m-1,d);
	
	comp = $("#"+arg0).val().split('/');
	m = parseInt(comp[0], 10);
	d = parseInt(comp[1], 10);
	y = parseInt(comp[2], 10);
	if(m != null && (m.toString().length == 2 || m.toString().length == 1)
			&& d != null && (d.toString().length == 2 || d.toString().length == 1)
			&& y != null && y.toString().length == 4){
		var otherEltDate = new Date(y,m-1,d);
		
		var lowerDate, upperDate;
		if(arg1 == true){//current element should be lower date
			lowerDate = currentEltdate;
			upperDate = otherEltDate;
		}else{
			lowerDate = otherEltDate;
			upperDate = currentEltdate;
		}
		var output=false;
		if(checkFlag === '='){
			output = (lowerDate <= upperDate);
		}
		else
		{
			output = (lowerDate < upperDate);
		}
		return this.optional(element) || (output);	
	} else {
		return true;
	}
	
});	
/**
 *  jquery validation method to validate date range
 **/
jQuery.validator.addMethod("DateRange", function(value, element, arg) {
	var arg0 = arg[0];
	var arg1 = arg[1];
	var comp = arg0.split('/');
	var m = parseInt(comp[0], 10);
	var d = parseInt(comp[1], 10);
	var y = parseInt(comp[2], 10);
	var startDate = new Date(y,m-1,d);
	
	comp = arg1.split('/');
	m = parseInt(comp[0], 10);
	d = parseInt(comp[1], 10);
	y = parseInt(comp[2], 10);
	var endDate = new Date(y,m-1,d);
	
	comp = value.split('/');
	m = parseInt(comp[0], 10);
	d = parseInt(comp[1], 10);
	y = parseInt(comp[2], 10);
	var date = new Date(y,m-1,d);
	// Start Updated in R5
	return this.optional(element) || ((startDate <= date) && (date <= endDate)  && date.getFullYear() == y);
	// End Updated in R5
});

/**
 * jquery validation method to validate date range
 **/
jQuery.validator.addMethod("calenderFutureDate", function(value, element) {
	var myDate1 = value;
	if(myDate1 != null && myDate1 != ''){
		var month  = myDate1.substring(0,2);
		var date = myDate1.substring(3,5);
		var year  = myDate1.substring(6,10);
		var currentDate = new Date(year,month-1,date);
		var today = new Date();
		month  = today.getMonth();
		date = today.getDate();
		year  = today.getFullYear();
		today = new Date(year,month,date);
		if (currentDate<=today){
			return this.optional(element) || false;
		}
	}
	return this.optional(element) ||  true;
});

/**
*   Start Added in R5
*	jquery validation method to restrict future date
**/
jQuery.validator.addMethod("calenderRestrictFutureDate", function(value, element) {
	var myDate1 = value;
	if(myDate1 != null && myDate1 != ''){
		var month  = myDate1.substring(0,2);
		var date = myDate1.substring(3,5);
		var year  = myDate1.substring(6,10);
		var currentDate = new Date(year,month-1,date);
		var today = new Date();
		month  = today.getMonth();
		date = today.getDate();
		year  = today.getFullYear();
		today = new Date(year,month,date);
		if (currentDate>today){
			return this.optional(element) || false;
		}
	}
	return this.optional(element) ||  true;
});
// End Added in R5

/**
*New method added as per enhancement 5402, build 2.6.0
*jquery validation method to validate date range based upon procurement status
*It validates if procurement status is 1(draft) or 2(planned) then updated dates cannot be less than today's date
*else for procuremnet status '3' released or more then updated dates cannot be less than (planned and today's date)
*parameters to this method are: updated date in 'value', procurement status in param[1], planned date in param[2]
**/
jQuery.validator.addMethod("calenderFutureDateBasedOnProcStts", function(value, element, param) {
	var updatedDate = value;
	if(updatedDate != null && updatedDate != ''){
		var month  = updatedDate.substring(0,2);
		var date = updatedDate.substring(3,5);
		var year  = updatedDate.substring(6,10);
		var updatedDateFormatted = new Date(year,month-1,date);
		
		var today = new Date();
		month  = today.getMonth();
		date = today.getDate();
		year  = today.getFullYear();
		today = new Date(year,month,date);
		if(param[1] != '' && (param[1] =='1' || param[1] =='2') ){
			if (updatedDateFormatted < today){
				return this.optional(element) || false;
			}
		}else{
			if(param[1] != ''){
				var plannedDate = param[2];
				month  = plannedDate.substring(0,2);
				date = plannedDate.substring(3,5);
				year  = plannedDate.substring(6,10);
				var plannedDateFormatted = new Date(year, month-1, date);
				if (updatedDateFormatted < plannedDateFormatted && updatedDateFormatted < today){
					return this.optional(element) || false;
			    }
			 }
		 }
	}
	return this.optional(element) ||  true;
});

/**
 * jquery validation method to validate date range
 **/
jQuery.validator.addMethod("numberFormatField", function(value, element) {
	value = value.replaceAll(",", "");
	if(value == 0) {
		return this.optional(element) ||  true;
	} else if(parseInt(value)){
		if(value.indexOf(".") >= 0) {
			return false;
		} else {
			return this.optional(element) ||  true;
		}
	} else {
		return false;
	}
});

/**
 *  jquery validation method to check if has decimal
 **/
jQuery.validator.addMethod('isDecimal', function (value, el, param) {
    return !(value.indexOf(".")>-1);
});

/**
 *  jquery validation method to check min value
 **/
jQuery.validator.addMethod('minStrict', function (value, el, param) {
	value = value.replaceAll(",", "");
    return value > param;
});
/**
 * jquery validation method to check min value
*  New Method in R4
*  */
jQuery.validator.addMethod('mini', function (value, el, param) {
	value = value.replaceAll(",", "");
    return value >= param;
});
/**
 *  jquery validation method to check max value
 * */
jQuery.validator.addMethod('maxStrict', function (value, el, param) {
	value = value.replaceAll(",", "");
    return value <= param;
});
/**
 *  jquery validation method to if value is a url or not
 **/
jQuery.validator.addMethod('isURL', function (value, el, param) {
	if(param == true || param =='true'){
		var linkStart = value.substring(0, value.length);
		if(value){
			linkStart = value.substring(0, value.length);
		} else {
			linkStart = value.substring(0, 8);
		}
		if(!(linkStart.indexOf("http://".substring(0, linkStart.length)) == 0 || linkStart.indexOf("https://".substring(0, linkStart.length)) == 0)){
			return false;
		}
	}
    return true;
});
/**
 * jquery validation method to check number max Length
 **/
jQuery.validator.addMethod('numberMaxLength', function (value, el, param) {
	value = value.replaceAll(",", "");
	return value.length <= param;
});

/**
 * jquery validation method to check currency max Length
 **/
jQuery.validator.addMethod('currencyMaxLength', function (value, el, arg) {
	var arg1 = arg[0];
	var arg2 = arg[1];
	value = value.replaceAll(",", "");
	var values=value.split(".");
	return (parseInt(values[0].length) <= parseInt(arg1) && (values[1]!=null && parseInt(values[1].length) <= parseInt(arg2)));
});



/**
 *  Check if start date is less than end date.
 **/
function checkStartEndDatePlanned(startDate, endDate){
	   if(startDate != '' && endDate != '' && (startDate > endDate))
		 return false;
	   else
		 return true;
}

/**
 * Check if start End date is not in future.
 **/
function checkStartEndDateNotFuture(startDate, endDate){
		var today = new Date();
	 if(today<startDate || today<endDate)
		 return false;
	   else
		 return true;
}
/**
 * Check if End date is before start date and display error message.
 **/
function displayMessageForStartEndDate(startEndDateFlag, object){
	if(!startEndDateFlag){
		object.parent().next().html("! The end date must be after the start date");
	}
}

/**
 *  This method removes the value selected in first dropdown from the second
 *	dropdown.
 **/
function removeOptionFromSecondDropDown(firstDropDownId, secondDropDownId){
	var firstDropDownOptions = jQuery(document.getElementById(firstDropDownId)).clone();
	var selectedValue = document.getElementById(firstDropDownId).value;
	var selectedValueSecond = document.getElementById(secondDropDownId).value;
	jQuery('option', firstDropDownOptions).each(function() {
        if (jQuery(this).val() == selectedValue) {
            jQuery(this).remove();
        }
    });
    $("#"+secondDropDownId + "").html(jQuery(firstDropDownOptions).html());
    if(selectedValue == selectedValueSecond)
	{
    	selectedValueSecond = "";
	}
    jQuery('option', $("#"+secondDropDownId + "")).each(function() {
        if (jQuery(this).val() == selectedValueSecond) {
            jQuery(this).attr("selected",true);
        }else{
        	jQuery(this).attr("selected",false);
        }
    });
}

/**
 *  Field Formatter Function with special character.
 **/
$.fn.fieldFormatterSpecial = function(format, charSet, allowedChars){
	this.change(function(){
		$(this).fieldFormatterFunctionSpecial(format, charSet, allowedChars);
	}).keyup(function(){
		$(this).fieldFormatterFunctionSpecial(format, charSet, allowedChars);
	});
};
/**
 *  Method for field Formatter with special character.
 **/
$.fn.fieldFormatterFunctionSpecial = function(format, charSet, allowedChars){
	var lsResult = null;
	if(charSet=="A"){
		lsResult = "0-9a-zA-Z";
	}else if(charSet=="T"){
		lsResult = "a-zA-Z";
	}else if(charSet=="N"){
		lsResult = "0-9";
	}
	var value = $(this).val();
	var preLength = value.length;
	var digits = value.replace(new RegExp("[^" + lsResult + allowedChars +"]", 'g'), "");
	var count = 0;
	var currentCursorPos = $(this).getCursorPosition();
	var valueToSet = format.replace(/X/g, function() {
		var value1=digits.charAt(count++);
		return value1;
	});
	var formatIncludes = format.replace(/X/g, '');
	for(var i=formatIncludes.length-1; i >= 0 ; i--){
		if(valueToSet && valueToSet != null && valueToSet.endsWith(formatIncludes.charAt(i))){
			valueToSet = valueToSet.substr(0, valueToSet.length - 1);
		}else{
			break;
		}
	}
	if(preLength < valueToSet.length){
		currentCursorPos = currentCursorPos +  valueToSet.length - preLength;
	}
	$(this).val(valueToSet).selectRange(currentCursorPos, currentCursorPos);
};	

/**
 *  This function call contactus Servlet based upon help category
 **/
function smFinancePageSpecificHelp(){
	 pageGreyOut();
	 var urlAppender = $("#contextPathSession").val()+"/ContactUsServlet.jsp?action=helpPage&screenName="+$("#screenName").val();
	   jQuery.ajax({
	   type: "POST",
	   url: urlAppender,
	   data: "",
	   success: function(e){
		   	$("#helpPageDiv").empty();
			$("#helpPageDiv").html(e);
			if($("#helpCategoryHidden").val()=='')
				$(".toplevelheaderHelp").html("Help Documents");
			else
			$(".toplevelheaderHelp").html($("#helpCategoryHidden").val() + " - Help Documents");
			$(".overlay").launchOverlay($(".alert-box-help"), $(".exit-panel"));
			removePageGreyOut();
	      },
	       beforeSend: function(){  
	  	}
	  });
};

$.fn.validateCurrencyNegative = function(){
	this.keyup(function(event) {
		extractNumber(event,this,2,true);
	});
};
$.fn.validateCurrencyPositive = function(){
	this.keyup(function(event) {
		extractNumber(event,this,2,false);
	});
};
/**
 * This method is called to perform formatting and to replace all non numbers
 **/
function extractNumber(evt, obj, decimalPlaces, allowNegative)
{
	var evt = evt || window.event;
	var target = evt.target || evt.srcElement;
	if(evt.keyCode == 37 || evt.keyCode == 39){
		return;
	}
    var temp = obj.value;
    // avoid changing things if already formatted correctly
    var reg0Str = '[0-9]*';
    if (decimalPlaces > 0) {
        reg0Str += '\[\,\.]?[0-9]{0,' + decimalPlaces + '}';
    } else if (decimalPlaces < 0) {
        reg0Str += '\[\,\.]?[0-9]*';
    }
    reg0Str = allowNegative ? '^-?' + reg0Str : '^' + reg0Str;
    reg0Str = reg0Str + '$';
    var reg0 = new RegExp(reg0Str);

    if(temp.indexOf('.')!=-1){  
    	var decimalValue = temp.split(".")[1];
        if(temp.charAt(0) == '-'){   
        	if(temp.split(".")[0].length > 17){
        		temp = temp.substring(0,17);
        		temp = temp + "."+decimalValue;
        	}
        }else{
        	if(temp.split(".")[0].length > 16){
        		temp = temp.substring(0,16);
        		temp = temp + "."+decimalValue;
        	}
        }
     }else{
		 if(temp.charAt(0) == '-'){
		 	if(temp.length>17){
		 		temp = temp.substring(0,17);
		 	}
	    }else{
	    	if(temp.length>16){
	    		temp = temp.substring(0,16);
	    	}
	    }
     } 
    
    if (reg0.test(temp)){
    }

    // first replace all non numbers
    var reg1Str = '[^0-9' + (decimalPlaces != 0 ? '.' : '') + (decimalPlaces != 0 ? ',' : '') + (allowNegative ? '-' : '') + ']';
    var reg1 = new RegExp(reg1Str, 'g');
    temp = temp.replace(reg1, '');
    
    if (allowNegative) {
        // replace extra negative
        var hasNegative = temp.length > 0 && temp.charAt(0) == '-';
        var reg2 = /-/g;
        temp = temp.replace(reg2, '');
        if (hasNegative) temp = '-' + temp;
    }

    if (decimalPlaces != 0) {
        var reg3 = /[\,\.]/g;
        var reg3Array = reg3.exec(temp);
        if (reg3Array != null) {
            // keep only first occurrence of .
            // and the number of places specified by decimalPlaces or the entire
			// string if decimalPlaces < 0
            var reg3Right = temp.substring(reg3Array.index + reg3Array[0].length);
            reg3Right = reg3Right.replace(reg3, '');
            reg3Right = decimalPlaces > 0 ? reg3Right.substring(0, decimalPlaces) : reg3Right;
            temp = temp.substring(0,reg3Array.index) + '.' + reg3Right;
           
        }
    }	
    obj.value = temp;
}
/**
 *  This method is called to block non numbers
 **/
function blockNonNumbers(obj, e, allowDecimal, allowNegative)
{
    var key;
    var isCtrl = false;
    var keychar;
    var reg;
    if(window.event) {
        key = e.keyCode;
        isCtrl = window.event.ctrlKey
    }
    else if(e.which) {
        key = e.which;
        isCtrl = e.ctrlKey;
    }

    if (isNaN(key)) return true;

    keychar = String.fromCharCode(key);

    // check for backspace or delete, or if Ctrl was pressed
    if (key == 8 || isCtrl)
    {
        return true;
    }

    reg = /\d/;
    var isFirstN = allowNegative ? keychar == '-' && obj.value.indexOf('-') == -1 : false;
    var isFirstD = allowDecimal ? keychar == '.' && obj.value.indexOf('.') == -1 : false;
    var isFirstC = allowDecimal ? keychar == ',' && obj.value.indexOf(',') == -1 : false;
    return isFirstN || isFirstD || isFirstC || reg.test(keychar);
}
/**
 * This method is called to block  Invalid characters.
 **/
function blockInvalid(obj)
{
    var temp=obj.value;
    if(temp=="-"){
        temp="";
    }
    if (temp.indexOf(".")==temp.length-1 && temp.indexOf(".")!=-1)
    {
        temp=temp+"00";
    }
    if (temp.indexOf(".")==0)
    {
        temp="0"+temp;
    }
    if (temp.indexOf(".")==1 && temp.indexOf("-")==0)
    {
        temp=temp.replace("-","-0") ;
    }
    if (temp.indexOf(",")==temp.length-1 && temp.indexOf(",")!=-1)
    {
        temp=temp+"00";
    }
    if (temp.indexOf(",")==0)
    {
        temp="0"+temp;
    }
    if (temp.indexOf(",")==1 && temp.indexOf("-")==0)
    {
        temp=temp.replace("-","-0") ;
    }
    temp=temp.replace(",",".") ;
    obj.value=temp;
}

$(function(){
	$("input[type='text']").each(function(){
		if($(this).attr("validate")=='currencyNegative'){
				$(this).validateCurrencyNegative();
		}
		else if($(this).attr("validate")=='currencyPositive'){
			$(this).validateCurrencyPositive();
		} 
	});
});
/**
 * Function sets the maximum length of Textarea.
 * method updated in R5
 */
function setMaxLength(obj,maxlimit){
	setMaxLengthForAddContract(obj,maxlimit);
}

/**
 * Function sets the maximum length of Textarea.
 * New Method in R4, method updated in R5
 * 
 */
function setMaxLengthForEvaluatePropasal(obj,maxlimit){
	setMaxLengthForAddContract(obj,maxlimit);
}
// Below function is added to fix defect 4453 as part of release 2.7.0
/**
 * Function sets the maximum length of Textarea for ass contract screen.
 * method updated in R5
 */
function setMaxLengthForAddContract(obj, maxlimit){
	if(($(obj).val().length + $(obj).val().split("\n").length-1)  > maxlimit){
		$(obj).val($(obj).val().substring(0, maxlimit - $(obj).val().split("\n").length + 1));
		return false;
	}
}

/**
 * This method remove the non required characters from input value.
 **/
function removeNonRequiredCharacter(obj)
{ 
	var val=$(obj).val();
	for(i=0;i<val.length;i++)
	{
     var code=val.charCodeAt(i);
     if(!(code>=32 && code<=126) && code!=8220 && code!=8216 && code!=167)
         {
    	 $(obj).val(""); 
    	 $(obj).parent().parent().find('span.error').html("Your response contains invalid characters. Please re-type your response or visit Online Help for instructions on how to Copy and Paste as plain text.");
    	 return true;
    	 }   
     else 
    	 {
    	 $(obj).parent().parent().find('span.error').html("");
    	 }
   }
}

/**
 * Date check on list screen.
 **/
function checkForDate(){
	var isValid = true ; 
	$("input[type='text']").each(function() {
		if ($(this).attr("validate") == 'calender') {
			isValid = verifyDate(this);
			if (!isValid) {
				return isValid;
			}
		}
		});
	return isValid;
}

/**
 * Date check on list screen.
 **/
function checkForDateContractList(){
	var isValid = true ; 
	var flag=false;
	$("input[type='text']").each(function() {
		if ($(this).attr("validate") == 'calender') {
			isValid = verifyDate(this);
			if (!isValid) {
				flag=true;
			}
		}
		});
	if(flag)isValid=false;
	else
		isValid=true;
	return isValid;
}
/**
 * This method is called to update Saved Data.
 **/
function updateSavedData(ignoreForms){
	lastDataArray = new Array();
	$("form").each(function(){
		if(typeof($(this).attr("name")) != "undefined" && $.inArray($(this).attr("name"), ignoreForms) < 0){
			lastDataArray[lastDataArray.length] = new Array($(this).attr("name"), $(this).serializeArray());
		}
	});
}
/**
 * This method is called to Allow Alpha Numerics in Epin.
 **/
function fnAllowAlphaNumericsEpin(e){
    var key;
    if(window.event)
    	key = window.event.keyCode;     //IE
	else
        key = e.which;                  //Firefox
    if ((key>47 && key<58) || (key>64 && key<91) || (key>96 && key<123) || key == 8 || key ==0){
    	return true;
    }else{
    	return false;
    }
}
$(function(){
	$('body').bind('ajaxSuccess',function(event,request,settings){
		if (request.getResponseHeader('REQUIRES_AUTH')!=null && request.getResponseHeader('REQUIRES_AUTH').startsWith("1")){
	       window.location = request.getResponseHeader('AUTH_PATH');
	    };
	});	
	$('body').bind('ajaxComplete',function(event,request,settings){
		 if (request.getResponseHeader('REQUIRES_AUTH')!=null && request.getResponseHeader('REQUIRES_AUTH').startsWith("1")){
			window.location = request.getResponseHeader('AUTH_PATH');
	    };
	});	
	$("input[type='text']").each(function(){
		if($(this).attr("validate")=='alphaNumericEpin'){
			$(this).keypress(function(event) {
				return fnAllowAlphaNumericsEpin(event);
			});
		}
	});
});
/**
 * This method is called to validate Comments.
 **/
function validateComment(){
	var returnFlag = true;
	if(document.getElementById("publicCommentArea")!=null){
	convertSpecialCharactersHTMLGlobal('publicCommentArea',true);
	var lsDescription =   $("#publicCommentArea").val();
	lsResult = "^[0-9a-zA-Z !@\\\#\\\$%^\\\&*()-_+=|\\\\{}\\\[\\\];:\\\"\\\'<>,.?\\\/`~\xA7]+$";
	var re = new RegExp(lsResult);
	if(null!=lsDescription && lsDescription!=""){
		returnFlag = re.test(lsDescription.replace(/\n/g, " "));
	}
	}
	if(returnFlag && document.getElementById("internalCommentArea")!=null){
		convertSpecialCharactersHTMLGlobal('internalCommentArea',true);
		var lsDescription =  $("#internalCommentArea").val();
		lsResult = "^[0-9a-zA-Z !@\\\#\\\$%^\\\&*()-_+=|\\\\{}\\\[\\\];:\\\"\\\'<>,.?\\\/`~\xA7]+$";
		var re = new RegExp(lsResult);
		if(null!=lsDescription && lsDescription!=""){
			returnFlag = re.test(lsDescription.replace(/\n/g, " "));
		}
	}
	return returnFlag;
	}
/**
 * This method is called to validate Task Comments.
 **/
function validateTaskComment(publicCommentArea, internalCommentArea){
	var returnFlag = true;
	if(document.getElementById(publicCommentArea)!=null){
	convertSpecialCharactersHTMLGlobal(publicCommentArea,true);
	var lsDescription =   $("#"+publicCommentArea).val();
	lsResult = "^[0-9a-zA-Z !@\\\#\\\$%^\\\&*()-_+=|\\\\{}\\\[\\\];:\\\"\\\'<>,.?\\\/`~\xA7]+$";
	var re = new RegExp(lsResult);
	if(null!=lsDescription && lsDescription!=""){
		returnFlag = re.test(lsDescription.replace(/\n/g, " "));
	}
	}
	if(returnFlag && document.getElementById(internalCommentArea)!=null){
		convertSpecialCharactersHTMLGlobal(internalCommentArea,true);
		var lsDescription =  $("#"+internalCommentArea).val();
		lsResult = "^[0-9a-zA-Z !@\\\#\\\$%^\\\&*()-_+=|\\\\{}\\\[\\\];:\\\"\\\'<>,.?\\\/`~\xA7]+$";
		var re = new RegExp(lsResult);
		if(null!=lsDescription && lsDescription!=""){
			returnFlag = re.test(lsDescription.replace(/\n/g, " "));
		}
	}
	return returnFlag;
	}
/**
 * This method is called to validate text written in Text Area.
 **/
function validateTextArea(id){
	var returnFlag = true;
	if(document.getElementById(id)!=null){
    convertSpecialCharactersHTMLGlobal(id,true);
	var lsDescription =  $("#"+id).val();
	lsResult = "^[0-9a-zA-Z !@\\\#\\\$%^\\\&*()-_+=|\\\\{}\\\[\\\];:\\\"\\\'<>,.?\\\/`~\xA7]+$";
	var re = new RegExp(lsResult);
	if(null!=lsDescription && lsDescription!=""){
		returnFlag = re.test(lsDescription.replace(/\n/g, "  "));
	}
	}
	return returnFlag;
	}

/**
 * This method is called to convert special character &.
 **/
function convertSpecialChar(str){
	if(typeof(str)!="undefined" && trim(str)!=""){
	str= str.replace(/\&/g,'%26');
	}
	return str;
}

/**
 * this method will trim the String
 **/
function trim(stringToTrim) {
	return stringToTrim.replace(/^\s+|\s+$/g,"");
}

/**
 * This will execute to apply css to table generated dynamically
 **/
function applyCssToHomePagesTable(tableId) {
	var counter = 0;
	$(tableId+" tr:visible").each(function() {
		if (counter % 2 != 0) {
			$(this).addClass("alternate");
		} 
		counter++;
	});
}

/**
*   Scroll to particular element
*   New Method in R4
**/
$.fn.scrollTo = function( target, options, callback ){
	  if(typeof options == 'function' && arguments.length == 2){ callback = options; options = target; }
	  var settings = $.extend({
	    scrollTarget  : target,
	    offsetTop     : 50,
	    duration      : 500,
	    easing        : 'swing'
	  }, options);
	  return this.each(function(){
	    var scrollPane = $(this);
	    var scrollTarget = (typeof settings.scrollTarget == "number") ? settings.scrollTarget : $(settings.scrollTarget);
	    var scrollY = (typeof scrollTarget == "number") ? scrollTarget : scrollTarget.offset().top + scrollPane.scrollTop() - parseInt(settings.offsetTop);
	    scrollPane.animate({scrollTop : scrollY }, parseInt(settings.duration), settings.easing, function(){
	      if (typeof callback == 'function') { callback.call(this); }
	    });
	  });
	};
	
/**
* checks if element is visble in scroll pane
*  New Method in R4
**/
function isScrolledIntoView(sourceElement, targetElement)
{
    var docViewTop = $(sourceElement).offset().top;
    var docViewBottom = docViewTop + $(sourceElement).height();

    var elemTop = $(targetElement).offset().top;
    var elemBottom = elemTop + $(targetElement).height();

    return ((elemBottom <= docViewBottom) && (elemTop >= docViewTop));
}

//R5 changes start
$(document).ready(function(){
	$(".graphHelp").on("click", function(e){
		e.stopPropagation();
		if($(this).find(".graphHelpTextClicked").size() == 0)
			$(this).find(".graphHelpText").addClass("graphHelpTextClicked");
		else
			$(this).find(".graphHelpText").removeClass("graphHelpTextClicked");
	});
	$(document).on("click", function(event){
		$(".graphHelpText").removeClass("graphHelpTextClicked");
		var target = event.target || event.srcElement;
		if(target !=null && typeof target !='undefined' && target.className != "curse"){
			$(".dataDictionarySuggestions").hide();
		}
	});
});
/**
 * This method is called to update alert inbox
 **/
function UpdateAlertInbox(){
	if($("#typeOfUser").attr("value") == 'agency_org' || $("#typeOfUser").attr("value") =='provider_org')
	{
		jQuery.ajax({
	        type : "POST",
	        url : "/HHSPortal/GetContent.jsp?alertAction=AlertData",
	        data : "",
	        contentType:"application/json; charset=utf-8",
	        dataType: "html",
	        success : function(e) {
	        	if(e != 0)
	        	{
	        		$('.alert_msg').text(e);
	        	}
			},
	        error: function() { 
	        	showErrorMessagePopup();
			}
		  });
	}
}

/**
 *  This Function redirect to Manage Orgnanization Module's Organization Information Tab
 *  onClick of provider's hypelink from Contract List, Budget List, Invoice List, Payment List, Amendment List Screens
 *  for city and agency only
*/
function viewOrganizationInformation(orgId, providerName){
	var _orgType = $("#orgType").val();
	var _contextPath = $('#contextPath').val();
	var _portal = "";
	var _portalInstance ="";
	if(_orgType == 'agency_org')
	{
		_portal = "portlet_hhsweb_portal_page_agency_home"; _portalInstance = "portletInstance_16";
	}
	else
	{
		_portal = "portlet_hhsweb_portal_page_city_home"; _portalInstance = "portletInstance_12";
	}
	window.location.href = _contextPath+"/portal/hhsweb.portal?_nfpb=true&_pageLabel="+_portal+"&_st=&_windowLabel="+_portalInstance+"&_urlType=action&providerId="+orgId+"~provider_org&providerName="+escapeProviderName(providerName);
}
/**
 * This method is called to escape Provider Name
 **/
function escapeProviderName(provName){
	return provName.replace(/&/g, "").replace(/</g, "").replace(/>/g, "").replace(/"/g, "").replace(/'/g, "");
}

jQuery.validator.addMethod("require_from_group", function(value, element, options) {
	var selector = options[1];
	var validOrNot = $(selector, element.form).filter(function () {
		  return $(this).val();
		 }).length >= options[0];
	return validOrNot;
});
jQuery.validator.addMethod("typeHeadDropDown", function(value, element, options) {
	if($(element).val()!='' && !($(element).typeHeadDropDown("isValid"))){
		return false;
	}
	else{
		return true;
	}
});
jQuery.validator.addMethod("autoCompleteOption", function(value, element, options) {
	if($(element).val()!=''){
		var suggestionVal = $(element).autoCompleteOptionSave("getSuggestions").suggestions;
		var uoValid = false;
		if (suggestionVal.length > 0) {
			for (var i = 0; i < suggestionVal.length; i++) {
				var arrVal = suggestionVal[i];
				if (arrVal == value) {
					uoValid = true;
					break;
				}
			}
		}
		return uoValid;
	}
	else{
		return true;
	}
});
jQuery.validator.addMethod("ValidateName", function(value, element, options) {
	if(value.toLowerCase().trim()=='recycle bin' || value.toLowerCase().trim()=='document vault' || value.toLowerCase().trim()=='recyclebin' || value.toLowerCase().trim()=='documentvault')
	{
		return false;
	}
	else
	{
		return true;
	}

});
if(typeof typeHeadDropDownData == "undefined")
	typeHeadDropDownData={};
$.fn.typeHeadDropDown = function(params) {
	var typeHeadDropDownLocal = new TypeHeadDropDown(this.get(0), params);
	return typeHeadDropDownLocal.returnedData;
};
	function TypeHeadDropDown(el, params){
		this.element = $(el);
		this.params =  {button:'', optionBox:''};
		var localId = guidGenerator()+$(el).attr("id");
		if(el.typeHeadDropDownId == null || typeof el.typeHeadDropDownId =='undefined' || typeof this[params] == 'undefined'){
			el.typeHeadDropDownId = localId;
			this.setOptions(params);
			typeHeadDropDownData[localId] = this.params;
			this.init();
		}else{
			localId = el.typeHeadDropDownId;
			var localParams = typeHeadDropDownData[localId];
			this.setOptions(localParams);
			this.returnedData = this[params]();
		}
	}
	TypeHeadDropDown.prototype = {
			init : function(){
				this.element.attr('autocomplete', 'off');
				this.refresh();
				this._setupHide();
				var el = this;
				this._keyUp();
				this.params.button.unbind("click").click(function(e){
					el.params.optionBox.find("li").hide();
					el.params.optionBox.find("li:containsNC('" +  el.element.val() +"')").show();
					el.params.optionBox.toggle();
					el.params.optionBox.parent().toggle();
					e.stopPropagation();
				});
			},
			isValid: function(){
				var el = this;
				var flag = false;
				var preservedValueTypeAhead = el.element.val();
				var similarOptions = el.params.optionBox.find("li:containsNC('" +  el.element.val() +"')");
				if(similarOptions != null && similarOptions.size()>0){
					similarOptions.each(function(){
						if($(this).text() == preservedValueTypeAhead){
							flag = true;
						}
					});
				}
				return flag;
			},
			refresh: function(){
				var el = this;
				var preservedValueTypeAhead = el.element.val();
				var preserveFlag = false;
				this.params.optionBox.find("li").each(function(){
					if($(this).text() == preservedValueTypeAhead){
						preserveFlag = true;
					}
				});
				if(!preserveFlag){
					el.element.val("");
				}
				this.params.optionBox.find("li").unbind("click").click(function(e){
					el.element.val($(this).text());
					el.params.optionBox.hide();
					el.params.optionBox.parent().hide();
					e.stopPropagation();
				});
				this.params.optionBox.find("li").unbind("mouseover").mouseover(function(){
					el.params.optionBox.find("li").removeClass("selectLiCombo");
					$(this).addClass("selectLiCombo");
				});
				this.params.optionBox.find("li").unbind("mouseout").mouseout(function(){
					$(this).removeClass("selectLiCombo");
				});
				return true;
			},
			_keyUp: function(){
				var el = this;
				el.element.on("keyup",function(){
					if($(this).val().length >= 3){
						el.params.optionBox.find("li").hide();
						if(el.params.optionBox.find("li:containsNC('" + el.element.val() +"')").length > 0)
						{
							el.params.optionBox.find("li:containsNC('" + el.element.val() +"')").show();
							el.params.optionBox.show();
							el.params.optionBox.parent().show();
						}else
						{
							el.params.optionBox.parent().hide();
						}
						
					}else{
						el.params.optionBox.find("li").show();
						el.params.optionBox.show();
						el.params.optionBox.parent().show();
					}
					
				});
			},
			_setupHide: function(){
				var el = this;
				$(document).click(function(){
					el.params.optionBox.hide();
					el.params.optionBox.parent().hide();
				});
			},
			setOptions: function(options){
			      this.params = options;
		    }
	};
	/**
	 * This method is called to generate guide
	 **/
	function guidGenerator() {
	    var S4 = function() {
	       return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
	    };
	    return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
	}
	$.extend($.expr[":"], {
		"containsNC": function(elem, i, match, array) {
		return (elem.textContent || elem.innerText || "").toLowerCase().indexOf((match[3] || "").toLowerCase()) >= 0;
		}
	});
	
	$.widget( "auto.autoCompleteOptionSave", {
		options: {
	        
	    },
	 
	    _create: function() {
	        this.options.searchedData = this.element.autocomplete(this.options);
	    },
	 
	    _setOption: function( key, value ) {
	        this.options[ key ] = value;
	    },
	    getSuggestions: function(){
	    	return this.options.searchedData;
	    },
	    updateOptions: function(options){
	    	var localOptions = this.options;
	    	$.each(options, function(key, value){
	    		localOptions[ key ] = value;
	    	});
	    	this.options.searchedData = this.element.autocomplete(this.options);
	    }
	    
	});
//R5 changes ends
	
// Start Added in R6
/**
 * This method return jsonObj and header name for csv file
 */
function JSONToCSVConvertor(obj, columnNames) {
	var arrData = "";
	if(obj != "" && $.trim((obj).length > 0))
	{
	  	arrData = typeof obj != 'object' ? JSON.parse(obj) : obj;
	}
 	var CSV = '';var row = "";
	for (var index in columnNames) {
		if($.trim(columnNames[index]) != ''){
			row += columnNames[index] + ',';
		}
	}
    row = row.slice(0, -1);CSV += row + '\r\n';
    for (var i = 0; i < arrData.length; i++) {
     var row = "";
     for (var index in arrData[i]) {
    	 if(index != 'id'){
    		var tmpRowVal = arrData[i][index];
    		if(!isNaN(tmpRowVal) && tmpRowVal.toString().indexOf('.') != -1){
    			tmpRowVal = parseFloat(tmpRowVal).toFixed(2);
    		}
    		row += '"' + tmpRowVal + '",';
    	 }
     }
     row.slice(0, row.length - 1);
     CSV += row + '\r\n';
 }
 return CSV;
}
//End Added in R6