// Start Added in R5
/**
 * This file contains utilities that are handled across various screens.
 * */
var dictionary=null, textareaDataArray = [], autoSaveDataTempArray = [];

var hddnDictionaryData="";
var hddnDictionaryAlgo="";
// End Added in R5
/**
 * This method validate for a number.
 **/
function validateNumber(input, event) {
	event = (event) ? event : window.event;
	var charCode = (event.which) ? event.which : event.keyCode;
	var commaAlreadyExists = (input.value.indexOf(".") != -1);
	var isComma = (charCode == 46);
	var isFirstChar = (input.value.length == 0);
	var isSecondChar = (input.value.length == 1);

	var isNumber = (charCode >= 48 && charCode <= 57);
	var controlKeys = [ 8, 9, 13, 35, 36, 37, 39, 45, 46 ];

	var isControlKey = false;
	// char "%" (36) "and" & (37) in IE8 have the same keyCode of
	// left row and right row!!
	if (BrowserDetect.browser == 'Explorer'  || BrowserDetect.browser=='Chrome' || BrowserDetect.browser=='Safari') {
		if (controlKeys.join(",").match(new RegExp(event.which)) != null) {
			isControlKey = false;
		} else {
			isControlKey = controlKeys.join(",").match(new RegExp(event.keyCode)) != null;
		}
	} else {
		isControlKey = controlKeys.join(",").match(new RegExp(event.keyCode)) != null;
	}
	// manage comma and zero
	if(isComma && commaAlreadyExists) return false;
	if(isComma && isFirstChar){
		input.value = '0.';
		return false;
	}
	if(isSecondChar && !isControlKey){
		if((input.value[0] == '0') && !isComma){
			input.value = '0.';
			return false;
		}
	}
	if ((isControlKey) || (isNumber) || (isComma)  || event.ctrlKey) {
		return true;
	}
	return false;
}
/**
 * this code handles the F5/Ctrl+F5/Ctrl+R
 **/
function blockRefresh(){
		document.onkeydown = function(e) {
	    var keycode;
	    if (window.event) {	
	        keycode = window.event.keyCode;
	    }else if (e) {
	        keycode = e.which;
	    }
	    if (BrowserDetect.browser == 'Explorer'  || BrowserDetect.browser=='Chrome' || BrowserDetect.browser=='Safari') {
	        if (keycode == 116 || (window.event.ctrlKey && keycode == 82)) {
	            window.event.returnValue = false;
	            window.event.keyCode = 0;
	            window.status = "Refresh is disabled";
	        }
	    }else{
	    	if (keycode == 116 ||(e.ctrlKey && keycode == 82)) {
	            if (e.preventDefault){
	                e.preventDefault();
	                e.stopPropagation();
	            }
	        }
	    }
	};
}

/**
 * This method check if input is a number.
 **/
function isNumber(input, event){
	event = (event) ? event : window.event;
	var charCode = (event.which) ? event.which : event.keyCode;
	var isNumber = (charCode >= 48 && charCode <= 57);
	var controlKeys = [ 8, 9, 13, 35, 36, 37, 39, 45, 46 ];

	var isControlKey = false;
	// char "%" (36) "and" & (37) in IE8 have the same keyCode of left row and
	// right row!!
	if (BrowserDetect.browser == 'Explorer' || BrowserDetect.browser=='Chrome' || BrowserDetect.browser=='Safari') {
		if (controlKeys.join(",").match(new RegExp(event.which)) != null) {
			isControlKey = false;
		} else {
			isControlKey = controlKeys.join(",").match(
					new RegExp(event.keyCode)) != null;
		}
	} else {
		isControlKey = controlKeys.join(",").match(new RegExp(event.keyCode)) != null;
	}
	if (isControlKey || isNumber || event.ctrlKey || event.metaKey) {
		return true;
	}
	return false;
}
/**
 * This method perform currency check.
 **/
$.fn.validateCurrency = function(){
	this.keypress(function(event) {
		if (validateNumber(this, event)) {
			return true;
		}
		return false;
	});
	this.blur(function(event) {
		var value = $(this).val().replaceAll(",", "");
		var values=value.split(".");
		if(value != ''){
			$(this).format( {
				format : "#,###.00",
				locale : "us"
			});
		}
	}); 
};
/**
 * Function to allow alphanumeric characters.
 **/
function fnAllowAlphaNumerics(e){
        var key;
        if(window.event)
        	key = window.event.keyCode;     //IE
		else
            key = e.which;                  //Firefox
        if ((key>47 && key<58) || (key>64 && key<91) || (key>96 && key<123) || key == 8 || key ==0 || key==44 || 
        					key==46 || key==34 || key==39 ||	key==95){
        	return true;
        }else{
        	return false;
        }
}
/**
 * Method to check for currecny.
 **/
$(function(){

	blockRefresh();

	//added to fix Defect#6136 to implement the global change for input fields
	$("input[type=text]").keyup(function(e){
		if(e.keyCode!=37 && e.keyCode!=38 && e.keyCode!=39 && e.keyCode!=40)
			replaceAllExceptAllowedChar(this);
	}).focusout(function(){
		replaceAllExceptAllowedChar(this);
	});


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
	            		 if(!(event.ctrlKey || event.metaKey))
	            			 validateDate(this,event);
	            		 return true;
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
			$(this).on("input paste", function (e) {
				var data = "";
				$.each($(this).val().replace(/[^0-9]/g, ''), function(index, value){
					data = data + value;
					if(index == 1 || index == 3){
						data = data + "/";
					}
				});
				$(this).val(data);
		    });
			$(this).blur(function(event) {
				verifyDate(this);
			});
		} else if($(this).attr("validate")=='calenderFormat'){
			$(this).keypress(function(event) {
				event = (event) ? event : window.event;
				var charCode = (event.which) ? event.which : event.keyCode;
				var isValid = isNumber(this,event);
	             if(isValid){
	            	 if(charCode==8 || charCode==46){
	            		return true; 
	            	 }else{
	            		 if(!(event.ctrlKey || event.metaKey))
	            			 validateDate(this,event);
	            		 return true;
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
		}
	});
	$("input, textarea").focus(function(){
		if($(this).is('[readonly]') || $(this).is(':disabled')){
			$(this).blur();
		}
	});
	$("a[href='#']").attr("href", "javascript:");
});
/**
 * Method to validate date.
 **/
function validateDate(d,e) {
	var pK = e ? e.which : window.event.keyCode;
	if (pK == 8) {
		d.value = substring(0,d.value.length-1); return;
	}
	var next = 20;
	var currenctDate = new Date();
	
	var currentYear = currenctDate.getFullYear();
	var prev = currentYear-1800;
	var prevYear = currentYear-prev;
	var nextYear = currentYear+next;
	
	var dt = d.value;
	var da = dt.split('/');
	for(var a = 0; a < da.length; a++){
		if (da[a] != +da[a]){
			da[a] = da[a].substr(0,da[a].length-1);
		}
	}
	if (da[0] > 12) {
		da[1] = da[0].substr(da[0].length-1,1);
		da[0] = '0'+da[0].substr(0,da[0].length-1);
	}
	if (da[1] > 31) {
		da[2] = da[1].substr(da[1].length-1,1);
		da[1] = '0'+da[1].substr(0,da[1].length-1);
	}
	if (da[2] > 9999){
		da[1] = da[2].substr(0,da[2].length-1);
	}
	dt = da.join('/');
	if (dt.length == 2 || dt.length == 5) {
		dt += '/';
	}
	d.value = dt;
}
/**
 * Method to get number of days in an year leap year case.
 **/
function getNumberOfDays(year, month) {
    var isLeap = ((year % 4) == 0 && ((year % 100) != 0 || (year % 400) == 0));
    return( [31, (isLeap ? 29 : 28), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][month]);
}
/**
 * Method to verify date if the entered date is valid or not.
 **/
function verifyDate(object) {
	
	if(object.value!='' && object.value.length < 10){
		$(object).parent().next().html("");
		$(object).parent().next().html('! Please enter a valid date');
		return false;
	}
	var next = 50;
	var currenctDate = new Date();
	
	var currentYear = currenctDate.getFullYear();
	var prev = currentYear - 1800;
	var prevYear = currentYear-prev;
	var nextYear = currentYear+next;

	var datevalue = $(object).val();
    var tmp = datevalue.split('/');
    var month = tmp[0];
    var date = tmp[1];
    var year = tmp[2];
    var validDate = "";
    
    $(object).parent().next().html("");
    if($(object).next().next().length>0){
    	$(object).parent().next().html("");
    }
    if(datevalue==''){
    	return true;
    }
	    if(parseFloat(month) >= 1 && parseFloat(month) <= 12){
	    	validDate =  getNumberOfDays(year,month-1);
	    }else{
	    	$(object).parent().next().html('! Please enter a valid date');
	        return false;
	    }
   
    if(parseFloat(date) >=1 && parseFloat(date) <=31){
    	if(parseFloat(date) <=parseFloat(validDate)){
	    	if(parseFloat(month) >= 1 && parseFloat(month) <= 12){
		    	if(parseFloat(year) >= parseFloat(prevYear) && parseFloat(year) <= parseFloat(nextYear)){
		    		return true;
		        } else {
		        	$(object).parent().next().html("! Invalid Date. Please enter a year equal to or after "+prevYear);
		            return false;
		        }
		     }else {
		    	 $(object).parent().next().html("! Please enter a valid date");
		    	 return false;
		     } 
	    }else{
	    	$(object).parent().next().html("! Date is not valid for a month");
            return false;
	    }
    }else {
    	$(object).parent().next().html("! Please enter a valid date");
        return false;
     }
}
/**
 * Listner method: it will handle the type of event that perform action accordingly.
 **/
function wlp_bighorn_attachEventHandler(target, type, handler)
{
	var result = false;
	if (target.addEventListener)
	{
		target.addEventListener(type, handler, false);
		result = true;
	}
	else if (target.attachEvent)
	{
		result = target.attachEvent("on" + type, handler);
	}
	else
	{
		var name = "on" + type;
		var old = (target[name]) ? target[name] : function() {};
		target[name] = function(e) { old(e); handler(e); };
		return true;
	}
	return result;
}

/**
 * Method to add class
 **/
function wlp_bighorn_addClassName(target, name)
{
	target.className += (target.className ? ' ' : '') + name;
}

/**
 * Method to remove class.
 **/
function wlp_bighorn_removeClassName(target, name)
{
	var regex = new RegExp(" ?" + name + "$");
	target.className = target.className.replace(regex, '');
}

var groupClass = "selectedSetting";
var otherClass = "noSelectSetting";
var groupCSS = "." + groupClass;
var otherCSS = "." + otherClass;
/**
 * Method to enable and disable checkbox
 **/
function enableDisableCheckBox(changedNode) {
	if (changedNode.hasClass(otherClass)) {
		if (changedNode.is(":checked")) {
			$(groupCSS).each(function() {
				$(this).attr("disabled", true).attr('checked', false);
			});
		} else {
			$(groupCSS).each(function() {
				$(this).attr("disabled", false);
			});
		}
	} else if (changedNode.hasClass(groupClass)) {
		var someChecked = false;
		$(groupCSS).each(function() {
			if ($(this).is(":checked")) {
				someChecked = true;
			}
		});
		if (someChecked) {
			$(otherCSS).attr("disabled", true).attr('checked', false);
		} else {
			$(otherCSS).attr("disabled", false);
		}
	}
}
/**
 * Method to greyout a pageout.
 **/
function pageGreyOut(){
	$('#greyedBackground').show();
}
/**
 * Method to greyout a page - Print Budget. Added for Release 3.4.0, #5681
 **/
function pageGreyOutPrintBudget(){
	$('#greyedBackgroundPrintBudget').show();
}
/**
 * Method to greyout a pageout on click of grid add button.
 **/
function pageGreyOutGrid(){
	$('#greyedBackground').show();
	setTimeout(function(){$('#greyedBackground').hide();},150);
}

/**
 * Method to remove greyout of a pageout.
 **/
var isUploadGreyOut = false;
function removePageGreyOut(){
	$('#greyedBackground').hide();
	if(isUploadGreyOut){
		isUploadGreyOut = false;
		$.unblockUI();
	}
}

/**
 * Method to remove greyout of a page - Print Budget.// Added for Release 3.4.0, #5681
 */
var isUploadGreyOutPrintBudget = false;
function removePageGreyOutPrintBudget(){
	$('#greyedBackgroundPrintBudget').hide();
	if(isUploadGreyOutPrintBudget){
		isUploadGreyOutPrintBudget = false;
		$.unblockUI();
	}
}

/**
 * Method to upload greyout of a pageout.
 * */
function uploadGreyOut(){
	var width = 600;
	var height = 220;
	var top = 0;
	var left = 0;
	if(!($(window).width() >= width && $(window).height() >= height)){
		top = $(document).scrollTop();
		left = $(document).scrollLeft();
	}else{
		top = ($(window).height() - height)/2 + $(document).scrollTop();
		left = ($(window).width() - width)/2 + $(document).scrollLeft();;
	}
	$.blockUI({ 
		message: "<div class='tabularCustomHead' style='text-align:left;'>Upload Document</div><div style='padding:40px 80px'><h2>Uploading Document...</h2><br/><img src='../framework/skins/hhsa/images/loading.gif' /><br/>This may take up to several minutes depending on document size.<br\>Do not refresh or navigate away from this page during this process.</div>",
		css: { 
			border: 'none', 
			opacity: 1, 
			color: '#999',
			width: width + 'px',
			height: height + 'px',
			top: top + 'px',
			left: left + 'px'
		} 
	});
	isUploadGreyOut = true;
}

String.prototype.endsWith = function(suffix) {
	return (this.indexOf(suffix, this.length - suffix.length) != -1);
};
String.prototype.startsWith = function(prefix) {
	return (this.indexOf(prefix) == 0);
};
String.prototype.ltrim = function(chars) {
	chars = chars || "\\s";
	return this.replace(new RegExp("^[" + chars + "]+", "g"), "");
};
String.prototype.rtrim = function(chars) {
	chars = chars || "\\s";
	return this.replace(new RegExp("[" + chars + "]+$", "g"), "");
};
String.prototype.trim = function(chars) {
	return this.ltrim(chars).rtrim(chars);
};
//replace all method
String.prototype.replaceAll = function(stringToFind,stringToReplace){
	var temp = this;
	var index = temp.indexOf(stringToFind);
	while(index != -1){
		temp = temp.replace(stringToFind,stringToReplace);
		index = temp.indexOf(stringToFind);
	}
	return temp;
};
//Field Formatter Function.
$.fn.fieldFormatter = function(format){
	this.change(function(){
		$(this).fieldFormatterFunction(format);
	}).keyup(function(){
		$(this).fieldFormatterFunction(format);
	});
};
//Method for field Formatter.
$.fn.fieldFormatterFunction = function(format){
	var value = $(this).val();
	var preLength = value.length;
	var digits = value.replace(/\D/g, '');
	var count = 0;
	var currentCursorPos = $(this).getCursorPosition();
	var valueToSet = format.replace(/X/g, function() {
		return digits.charAt(count++);
	});
	var formatIncludes = valueToSet.replace(/[^\D]/g, '');
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
//Method to get cursor position.
$.fn.getCursorPosition = function() {
	var el = $(this).get(0);
	var pos = 0;
	if('selectionStart' in el) {
		pos = el.selectionStart;
	} else if('selection' in document) {
		el.focus();
		var Sel = document.selection.createRange();
		var SelLength = document.selection.createRange().text.length;
		Sel.moveStart('character', -el.value.length);
		pos = Sel.text.length - SelLength;
	}
	return pos;
};
//Method to select a range.
$.fn.selectRange = function(start, end) {
	return this.each(function() {
		if (this.setSelectionRange) {
			this.focus();
			this.setSelectionRange(start, end);
		} else if (this.createTextRange) {
			var range = this.createTextRange();
			range.collapse(true);
			range.moveEnd('character', end);
			range.moveStart('character', start);
			range.select();
		}
	});
};
//Method for outer html.
$.fn.outerHTML = function(s) {
	return s
	? this.before(s).remove()
			: jQuery("<p>").append(this.eq(0).clone()).html();
};
/**
 * method to show a bix
 * */
function showMe(it, box) {
	if(box.id=='box'){
		vis = "none";
	}else{
		vis = "block";
	}
	document.getElementById(it).style.display = vis;
}

var overlayLaunched = null;
var alertboxLaunched = null;
var newLaunch = false;
//function to launch overlay.
$.fn.launchOverlay = function(alertbox, close, width, height, callBack) {
	overlayLaunched = $(this);
	alertboxLaunched = alertbox;
	var pageW = $(document).width();
	var pageH = $(document).height();
	if(width!=null && typeof(width)!="undefined"){
		alertboxLaunched.width(width);
	}
	if(height!=null && typeof(height)!="undefined"){
		alertboxLaunched.height(height);
	}
	alertboxLaunched.show();
	overlayLaunched.show().width(pageW).height(pageH);
	newLaunch = true;
	$(window).resize();
	close.unbind('click').click(function(e){
		if(overlayLaunched != null){
			e.stopPropagation();
			overlayLaunched.closeOverlay(e);
		}
	});
	callBackInWindow(callBack);
};
/**
 * Method to call Back In Window.
 * */
function callBackInWindow(callBack){
	if (callBack!=null && typeof(callBack)!="undefined" && callBack in window) {
		window[callBack]();
	}
}
//Method to close overlay.
$.fn.closeOverlay = function(e) {
	if(overlayLaunched != null){
		alertboxLaunched.hide();
		overlayLaunched.hide();
		alertboxLaunched = null;
		overlayLaunched = null;
	}
	callBackInWindow("closePopUp");
};
//Method to resize window.
$(window).resize(function() {
	if(overlayLaunched != null){
		var pageW = $(document).width();
		var pageH = $(document).height();
		overlayLaunched.width(pageW).height(pageH);
		var leftToSet = ($(window).width() - alertboxLaunched.outerWidth())/2 + $(document).scrollLeft();
		var topToSet = ($(window).height() - alertboxLaunched.outerHeight())/2 + $(document).scrollTop();
		if(newLaunch){
			if(!($(window).width() >= alertboxLaunched.outerWidth() && $(window).height() >= alertboxLaunched.outerHeight())){
				leftToSet = $(document).scrollLeft();
				topToSet = $(document).scrollTop();
			}
		}
		if(newLaunch || ($(window).width() >= alertboxLaunched.outerWidth() && $(window).height() >= alertboxLaunched.outerHeight())){
			alertboxLaunched.css({
				position:'absolute',
				left: leftToSet,
				top: topToSet
			});
			if(newLaunch){
				newLaunch = false;
			}
		}
	}
	if(isUploadGreyOut){
		var headDiv = $(".tabularCustomHead:visible").parent();
		var width = 600;
		var height = 220;
		var left = 0;
		var top = 0;
		if(!($(window).width() >= width && $(window).height() >= height)){
			top = $(document).scrollTop();
			left = $(document).scrollLeft();
		}else{
			top = ($(window).height() - height)/2 + $(document).scrollTop();
			left = ($(window).width() - width)/2 + $(document).scrollLeft();;
		}
		headDiv.css({"top" : top,
			"left": left, "position" : "absolute"});
	}
});
//method to scroll a window.
$(window).scroll(function () {
	$(window).resize(); 
});
/**
 * Method for max length check.
 **/
function ismaxlength(obj){
	var mlength=obj.getAttribute? parseInt(obj.getAttribute("maxlength")) : "";
	if (obj.getAttribute && obj.value.length>mlength)
		obj.value=obj.value.substring(0,mlength);
}
/**
 * Method to refresh a page.
 **/
function refresh() {
	window.location.reload(true);
}
/**
 * Method for scrollable function.
 * */
$.fn.scrollableTable = function(height) {
	var oldWidth = $(this).width();
	var headerTr = $(this).find("th:nth-child(1)").parent();
	var newTable = $(document.createElement('table')); 
	newTable.html("<tr><td><table></table></td></tr><tr><td><div><table></table></div></td></tr>");
	newTable.find("table").eq(0).append(headerTr.clone());
	$(this).find("tr").filter(function() {
		return $(this).find('td').size() > 0;
	}).each(function(){
		newTable.find("table").eq(1).append($(this));
	});
	$(this).parent().append(newTable);
	var currentHeight  = newTable.find("table").eq(1).height();
	if(height!=null && typeof(height)!="undefined" && currentHeight > height){
		newTable.find("div").eq(0).css({"height" : height+"px",
			"overflow": "auto"	})
			.width(oldWidth);
		newTable.find("table").width(oldWidth - 30);
	}
	var i = 1;
	newTable.find("table").eq(0).find("th").each(function(){
		var thWidth = $(this).width();
		newTable.find("table").eq(1).find("tr:nth-child(1)").find("td:nth-child("+i+++")").eq(0).width(thWidth);
	});
	$(this).remove();
};
/**
 * Method to change size of a div.
 * */
function changemysize(elt, myvalue)
{
	if($("#smallA").size() > 0){
		var div = document.getElementById("mymain");
		div.style.fontSize = myvalue + "px";
		$("li.activetext").removeClass("activetext");
		$(elt).parent().addClass("activetext");
		if($("#aaaValueToSet").val() != myvalue && typeof($("#urlForAAA").val()) != 'undefined'){
			var url = $("#urlForAAA").val()+"&aaaValueToSet="+myvalue;
			$("#aaaValueToSet").val(myvalue);
			var jqxhr = $.ajax( {
				url : url,
				type : 'POST',
				cache : false,
				dataType: "html",
				success : function(data) {
					return false;
				}
			});
		}
	}
}
//Method to compare.d
jQuery.fn.compare = function(t) {
	if (this.length != t.length) { return false; }
	var a = this.sort(),
	b = t.sort();
	for (var i = 0; t[i]; i++) {
		if (a[i].name != b[i].name || a[i].value != b[i].value) { 
			return false;
		}
	}
	return true;
};
/**
 * Method to supress a back space.
 * */
function suppressBackspace(evt) {
	evt = evt || window.event;
	var target = evt.target || evt.srcElement;
	if (evt.keyCode == 8 && !/input|textarea/i.test(target.nodeName)) {
		return false;
	}
}    
/**
 * Method called when a page get loaded.
 **/
$(document).ready(function(){
	$("input[type='radio'],  input[type='checkbox']").keydown(function(evt){
		var keyCode = evt ? (evt.which ? evt.which : evt.keyCode) : event.keyCode;
        if (keyCode == 13) { 
        	return false;
        }
	});
	//R5 changes start
	$(document).ajaxSuccess(function (evt, jqXHR, settings) {
		  if($('textarea').size() > 0)
		  {
			  setTimeout(function(){populateAutoSavedData();}, 1000);
		  }
	});
	$('*').on("scroll", function(event){
		$('.dataDictionarySuggestions').hide();
	});
	$(document).on("scroll", function(event){
		$('.dataDictionarySuggestions').hide();
	});
	//R5 changes ends
});
//Key doen functions.
$(function(){
	$(document).keydown(function(e){
	    var elid = $(document.activeElement).is("input, textarea, div[id$='text']"); // changed in R5 removed textarea
	    if(e.keyCode === 8 && !elid){
	       return false; 
	    };
	});
	
	if($(".overlay").length > 0){
		$(".overlay").focus();
	}
});

jQuery.extend( jQuery.fn, {
    // Name of our method & one argument (the parent selector)
    hasParent: function(p) {
        // Returns a subset of items using jQuery.filter
        return (this.filter(function(){
            // Return truthy/falsey based on presence in parent
            return $(p).find(this).length;
        }).size() > 0);
    }
});
// for AAA functionality
$(function(){
	var mySizeFromPage = $("#aaaValueToSet").val();
	var mySize = 12;
	if(typeof(mySizeFromPage) != 'undefined' && mySizeFromPage.length > 0)
		mySize = parseInt(mySizeFromPage);
	if(mySize == 10){
		changemysize(document.getElementById("smallA"), 10);
	}else if(mySize == 14){
		changemysize(document.getElementById("largeA"), 14);
	}else {
		changemysize(document.getElementById("mediumA"), 12);
	}
});
/**
 * This method imposes maxlength on textarea (for formbuilder)
 * */
function imposeMaxLength(Event, elementObj, element)
{
	var parentTag=elementObj.parentNode; 
	var maxLength = parentTag.maxlength;
	if (typeof(maxLength)== "undefined"){
		for( var x = 0; x < parentTag.attributes.length; x++ ) {
			if( parentTag.attributes[x].nodeName.toLowerCase() == 'maxlength' ) {
				maxLength = parentTag.attributes[x].nodeValue ;
			}
		}
	}
	var elementReturn;
	if(elementObj.nodeName.toLowerCase() == 'textarea')
	{
		elementReturn = (elementObj.value.length < maxLength);
		if(!elementReturn)
			elementObj.value = elementObj.value.substr(0, maxLength);
	}
	else{
		elementReturn = ((Event.keyCode == 9) || ((elementObj.value.length < maxLength)||(Event.keyCode == 8 ||Event.keyCode==46|| (Event.keyCode>=35&&Event.keyCode<=40))));
	}
	return elementReturn;
}
/**
 * This function is added to show Error Message Popup on screen.
 **/
function showErrorMessagePopup(){
	$('<div id="dialogBox"></div>').appendTo('body')
    .html('<div><h6>Some Internal Error occured.<br />Please try again after some time.</h6></div>')
    .dialog({
          modal: true, title: 'Internal Error', zIndex: 10000, autoOpen: true,
          width: 'auto', modal: true, resizable: false, draggable:false,
          dialogClass: 'dialogButtons',
          buttons: {
                Ok: function () {
                	$(this).remove();
                }
          },
          close: function (event, ui) {
                $(this).remove();
          }
    });
}

/**
 * This function acts as wlp_bighorn_float_handler
 * */
function wlp_bighorn_float_handler(button)
{
    var doAction = true;

    if (window.wlp_bighorn_float)
    {
        doAction = wlp_bighorn_float(button);
    }
    else
    {
        button.target = '_blank';
    }

    return doAction;
}

/**
 * This function acts as wlp_bighorn_delete_handler
 * */
function wlp_bighorn_delete_handler(button)
{
    var doAction = true;

    if (window.wlp_bighorn_delete)
    {
        doAction = wlp_bighorn_delete(button);
    }
    else
    {
        doAction = wlp_deleteButtonDialog(button);
    }

    return doAction;
}


/**
 * This function launches the contact us popup on click of contact us link
 * */
function contactUsClick(){
		pageGreyOut();
		var pageW = $(document).width();
		var pageH = $(document).height(); 
		     $(".overlay").show();
			 $(".alert-box-contact").show();
			 $(".overlay").width(pageW);
			 $(".overlay").height(pageH);
			 
      var v_parameter="";
    	var urlAppender = $("#contextPathSession").val()+ '/ContactUsServlet.jsp';
         jQuery.ajax({
         type: "POST",
         url: urlAppender,
         data: v_parameter,
         success: function(e){
               $("#contactDiv").empty();
			   $("#contactDiv").html(e);
			     removePageGreyOut();
            },
            beforeSend: function(){  
            },
            complete: function(){  
                $("#helpButton").hide();
            }
          });
}

/**
 * This function launches the contact us popup on click of contact us link from help page
 **/
function contactUsClickFromHelp(helpCategory){
	pageGreyOut();
	$(".alert-box-help").hide();
	var urlAppender = $("#contextPathSession").val()+"/ContactUsServlet.jsp?helpCategory="+helpCategory;
     jQuery.ajax({
     type: "POST",
     url: urlAppender,
     data: "",
     success: function(e){
           $("#contactDiv").empty();
		   $("#contactDiv").html(e);
		   removePageGreyOut();
		   $("").launchOverlay($(".alert-box-contact"), $(".exit-panel"));
        },
        beforeSend: function(){  
        
        },
        complete: function(){  
            $("#cancelbutton").hide();
        }
      });
}

/**
 * This function call contactus Servlet based upon help category
 **/
function pageSpecificHelp(helpCategory){
	if($("input[validate='calender']:visible").size()>0){
		callBackInWindow("closePopUp");
	}
	 pageGreyOut();
	 var urlAppender = $("#contextPathSession").val()+"/ContactUsServlet.jsp?action=helpPage&helpCategory="+helpCategory;
	   jQuery.ajax({
	   type: "POST",
	   url: urlAppender,
	   data: "",
	   success: function(e){
		   	$("#helpPageDiv").empty();
			$("#helpPageDiv").html(e);
			$(".overlay").launchOverlay($(".alert-box-help"), $(".exit-panel"));
			removePageGreyOut();
	      },
	       beforeSend: function(){  
	  	}
	  });	   
};

/**
 * This function is used to view document based upon document id and document name
 * R5 changes start - added check for document exits or not before download
 **/
function viewDocument(documentId, documentName){
	var selectedFolderId ;
	var divId;
	var selectedOrgType = '';
	var selectedOrgId = '';
	if($('#leftTree').val() != undefined)
	{
		if($js("#leftTree").jstree(true))
		{
			selectedFolderId = $js("#leftTree").jstree("get_selected");
			divId = 'leftTree';
		}
	else
		{
			selectedFolderId = $js("#selectOrgnization").jstree("get_selected");
			divId = 'selectOrgnization';
		}
	}
	var jqxhr = $.ajax( {
		url : $("#contextPathSession").val()+"/GetContent.jsp?action=checkDocExits&documentId="+documentId+"&documentType=file"+"&jspName=provdoclist",
		type : 'POST',
		success : function(data) {
			if(data == 'FilenotFound'){
				var _message= "The selected file has been deleted.";
				openRootNodeInTree('leftTree');
				$(".messagediv").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
		  		$(".messagediv").addClass("failed");
		  		$(".messagediv").show();
				
			}else{
				window.location.href = $("#contextPathSession").val()+"/GetContent.jsp?action=displayDocument&documentId="+documentId+"&documentName="+documentName;
			}
		},
		error : function(data, textStatus, errorThrown) {
		},
		complete : function() {
		}
	});
	
};
//R5 end
/**
 * This function is used to view document based upon document type and document name
 **/
function viewDocumentByType(documentId, documentName){
	window.open($("#contextPathSession").val()+"/GetContent.jsp?action=displayAppendix&documentType="+documentId+"&documentName="+documentName);
};

/**
 * This function is used to view document based upon document id
 **/
function viewDocumentTask(documentId,docName){
	var url=$("#contextPathSession").val()+"/GetContent.jsp?action=displayDocument&documentId="+documentId+"&documentName="+docName;
	previewUrl(url,'linkDiv');
};

var idleTime = ''; // number of miliseconds until the user is considered idle
var modelPopup = '<div id="sessionTimeoutWarning" style="display: none"></div>';
var initialSessionTimeoutMessage = '<div class="sessionTimeOutLogo">Your HHS Accelerator session will end in   '+ 
	'<span id="sessionTimeoutCountdown"></span>&nbsp;<br/><br/>Click <b>Continue</b> '+
	'if you would like to continue using HHS Accelerator. <br/> Click <b>Exit</b> if you would like to exit HHS Accelerator.</div>';
var sessionTimeoutCountdownId = 'sessionTimeoutCountdown';
var redirectAfter = 10; // number of seconds to wait before redirecting the user
var redirectTo = '';//'?_nfpb=true&_pageLabel=portlet_hhsweb_portal_login_page&_nfls=false&logout=logout';
var keepAliveURL = '/js_sandbox/'; // URL to call to keep the session alive
var expiredMessage = 'Your session has expired.  You are being logged out for security reasons.'; // message to show user when the countdown reaches 0
var running = false; // var to check if the countdown is running
var timer; // reference to the setInterval timer so it can be stopped
$(document).ready(function() {
	if($("#sessionTimeOutLogin").val() && $("#sessionTimeOutLogin").val()!='false'){
		if($("#typeOfUser").attr("value")=='provider_org'){
			redirectTo = $("#contextPathSession").attr("value")+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_login_page&_nfls=false&logout=logout&app_menu_name=logout_icon" ;
		}else{
			// start QC 9205 R 8.0.0 Internal SAML 
			//redirectTo = $("#contextPathSession").attr("value")+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_login_page&_nfls=false&logout=logout&siteminderLogout=siteminderLogout&app_menu_name=logout_icon";
			redirectTo = $("#contextPathSession").attr("value")+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_login_page&_nfls=false&logout=logout&app_menu_name=logout_icon" ;
			// end QC 9205 R 8.0.0 Internal SAML 
		}
		idleTime = ($("#sessionTimeOut").val()-parseInt(2))*60*1000;// session time out defined in hhs properties 
		redirectAfter = 120; // 2 mins in seconds;
	// create the warning window and set autoOpen to false
	var sessionTimeoutWarningDialog = $(modelPopup);
	$(sessionTimeoutWarningDialog).html(initialSessionTimeoutMessage);
	$(sessionTimeoutWarningDialog).dialog({
		title: 'HHS Accelerator TimeOut',
		autoOpen: false,	// set this to false so we can manually open it
		closeOnEscape: false,
		zIndex:20000,
		draggable: false,
		width: 626,
		minHeight: 70, 
		modal: true,
		dialogClass: 'dialogButtons',
		beforeclose: function() { // bind to beforeclose so if the user clicks on the "X" or escape to close the dialog, it will work too
			// stop the timer
			clearInterval(timer);
			// stop countdown
			running = false;
			// ajax call to keep the server-side session alive
			$.ajax({
			  url: keepAliveURL,
			  async: false
			});
		},
		buttons: {
			Continue: function() {
				// close dialog
				$(this).dialog('close');
			},
			Exit: function() {
				// close dialog
				location.href= redirectTo;
			}
		},
		resizable: false,
		open: function() {
			// scrollbar fix for IE
			$('body').css('overflow','hidden');
		},
		close: function() {
			// reset overflow
			$('body').css('overflow','auto');
		}
	}); // end of dialog
	$("div.dialogButtons div button:nth-child(1)").attr("title","Continue");
	$("div.dialogButtons div button:nth-child(2)").attr("title","Exit");
	// start the idle timer
	$.idleTimer(idleTime);
	// bind to idleTimer's idle.idleTimer event
	$(document).bind("idle.idleTimer", function(){
		// if the user is idle and a countdown isn't already running
		if($.data(document,'idleTimer') === 'idle' && !running){
			var counter = redirectAfter;
			running = true;
			// intialisze timer
			var timeToDisplay = redirectAfter;
			timeToDisplay = (timeToDisplay/60)+":00";
			$('#'+sessionTimeoutCountdownId).html(timeToDisplay);
			// open dialog
			$(sessionTimeoutWarningDialog).dialog('open');
			// create a timer that runs every second
			timer = setInterval(function(){
				counter -= 1;
				// if the counter is 0, redirect the user
				if(counter === 0) {
					$(sessionTimeoutWarningDialog).html(expiredMessage);
					$(sessionTimeoutWarningDialog).dialog('disable');
					location.href= redirectTo;
				} else {
					timeToDisplay = counter;
					minutes = parseInt( timeToDisplay / 60 ) % 60;
					seconds = timeToDisplay % 60;
					$('#'+sessionTimeoutCountdownId).html(minutes+":"+(seconds<=9?"0"+seconds:seconds));
				};
			}, 1000);
		};
	});
	}
	//R5 changes starts
	enableAutoSave();
	enableDictionary();
	//R5 changes ends
});

(function($){
$.idleTimer = function f(newTimeout){
    var idle    = false,        //indicates if the user is idle
        enabled = true,        //indicates if the idle timer is enabled
        timeout = 30000,        //the amount of time (ms) before the user is considered idle
        events  = 'mousemove keydown DOMMouseScroll mousewheel mousedown', // activity is one of these events
    toggleIdleState = function(){
        //toggle the state
        idle = !idle;
        // reset timeout counter
        f.olddate = +new Date;
        //fire appropriate event
        $(document).trigger(  $.data(document,'idleTimer', idle ? "idle" : "active" )  + '.idleTimer');            
    },
    /**
     * Stops the idle timer. This removes appropriate event handlers
     * and cancels any pending timeouts.
     * @return {void}
     * @method stop
     * @static
     */         
    stop = function(){
        //set to disabled
        enabled = false;
        //clear any pending timeouts
        clearTimeout($.idleTimer.tId);
        //detach the event handlers
        $(document).unbind('.idleTimer');
    },
    /* (intentionally not documented)
     * Handles a user event indicating that the user isn't idle.
     * @param {Event} event A DOM2-normalized event object.
     * @return {void}
     */
    handleUserEvent = function(){
        //clear any existing timeout
        clearTimeout($.idleTimer.tId);
        //if the idle timer is enabled
        if (enabled){
            //if it's idle, that means the user is no longer idle
            if (idle){
                toggleIdleState();           
            } 
            //set a new timeout
            $.idleTimer.tId = setTimeout(toggleIdleState, timeout);
        }    
     };
    /**
     * Starts the idle timer. This adds appropriate event handlers
     * and starts the first timeout.
     * @param {int} newTimeout (Optional) A new value for the timeout period in ms.
     * @return {void}
     * @method $.idleTimer
     * @static
     */ 
    f.olddate = f.olddate || +new Date;
    //assign a new timeout if necessary
    if (typeof newTimeout == "number"){
        timeout = newTimeout;
    } else if (newTimeout === 'destroy') {
        stop();
        return this;  
    } else if (newTimeout === 'getElapsedTime'){
        return (+new Date) - f.olddate;
    }
    //assign appropriate event handlers
    $(document).bind($.trim((events+' ').split(' ').join('.idleTimer ')),handleUserEvent);
    //set a timeout to toggle state
    $.idleTimer.tId = setTimeout(toggleIdleState, timeout);
    // assume the user is active for the first x seconds.
    $.data(document,'idleTimer',"active");
}; // end of $.idleTimer()
})(jQuery);

/**
 * Method for rendering a page.
 **/
function hhsAjaxRender(/*object*/event, /*object*/p_form, /*jsonObj*/options, url, callbackMethodName) {
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

		cform.removeChild(createdTmpHidden);

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
			}
		});
		
	}
/**
 * This method is called to retrieve clicked object 
 **/
function getClickedObj(/*string*/clickedObjName, /*object*/cform) {
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
 * Method to create ajax input.
 **/
function createAjaxInput(/*object*/element) {
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
 * Method to refresh element using there ids.
 * */
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
 * Method hhs Ajax Call back.
 * */
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
 * This function is used to detect the browser in which application is getting accessed
 */
var BrowserDetect = {
	init: function () {
		this.browser = this.searchString(this.dataBrowser) || "An unknown browser";
		this.version = this.searchVersion(navigator.userAgent)
			|| this.searchVersion(navigator.appVersion)
			|| "an unknown version";
		this.OS = this.searchString(this.dataOS) || "an unknown OS";
	},
	searchString: function (data) {
		for (var i=0;i<data.length;i++)	{
			var dataString = data[i].string;
			var dataProp = data[i].prop;
			this.versionSearchString = data[i].versionSearch || data[i].identity;
			if (dataString) {
				if (dataString.indexOf(data[i].subString) != -1)
					return data[i].identity;
			}
			else if (dataProp)
				return data[i].identity;
		}
	},
	searchVersion: function (dataString) {
		var index = dataString.indexOf(this.versionSearchString);
		if (index == -1) return;
		return parseFloat(dataString.substring(index+this.versionSearchString.length+1));
	},
	dataBrowser: [
		{
			string: navigator.userAgent,
			subString: "Chrome",
			identity: "Chrome"
		},
		{ 	string: navigator.userAgent,
			subString: "OmniWeb",
			versionSearch: "OmniWeb/",
			identity: "OmniWeb"
		},
		{
			string: navigator.vendor,
			subString: "Apple",
			identity: "Safari",
			versionSearch: "Version"
		},
		{
			prop: window.opera,
			identity: "Opera",
			versionSearch: "Version"
		},
		{
			string: navigator.vendor,
			subString: "iCab",
			identity: "iCab"
		},
		{
			string: navigator.vendor,
			subString: "KDE",
			identity: "Konqueror"
		},
		{
			string: navigator.userAgent,
			subString: "Firefox",
			identity: "Firefox"
		},
		{
			string: navigator.vendor,
			subString: "Camino",
			identity: "Camino"
		},
		{		// for newer Netscapes (6+)
			string: navigator.userAgent,
			subString: "Netscape",
			identity: "Netscape"
		},
		{
			string: navigator.userAgent,
			subString: "MSIE",
			identity: "Explorer",
			versionSearch: "MSIE"
		},
		{
			string: navigator.userAgent,
			subString: "Gecko",
			identity: "Mozilla",
			versionSearch: "rv"
		},
		{ 		// for older Netscapes (4-)
			string: navigator.userAgent,
			subString: "Mozilla",
			identity: "Netscape",
			versionSearch: "Mozilla"
		}
	],
	dataOS : [
		{
			string: navigator.platform,
			subString: "Win",
			identity: "Windows"
		},
		{
			string: navigator.platform,
			subString: "Mac",
			identity: "Mac"
		},
		{
			   string: navigator.userAgent,
			   subString: "iPhone",
			   identity: "iPhone/iPod"
	    },
		{
			string: navigator.platform,
			subString: "Linux",
			identity: "Linux"
		}
	]

};
BrowserDetect.init();

/**
 * This function converts MS Word characters to HTML characters
 * */
function convertSpecialCharactersHTMLGlobal(elementId,checkBackSlash, returnFlag){
	var elementValue = $("#"+elementId).val();
	if(typeof elementValue != "undefined" && elementValue != null){
		// Check for Smart Double Quotes
		elementValue = elementValue.replace(/[\u201C|\u201D|\u201E]/g, "\"");
		// Check for Single Quotes
		elementValue = elementValue.replace(/[\u2018|\u2019|\u201A]/g, "\'");
		// Check for Bullet
		elementValue = elementValue.replace(/[\u2022|\u00B7|\uF0B7]/g, "*");
	    // ellipsis
		elementValue = elementValue.replace(/[\u2026]/g, "...");
	    // dashes
		elementValue = elementValue.replace(/[\u2013|\u2014]/g, "-");
		// circumflex
		elementValue = elementValue.replace(/\u02C6/g, "^");
	    // spaces
		elementValue = elementValue.replace(/[\u02DC|\u00A0|\u0020]/g, " ");
		elementValue = elementValue.replace(/\t/g, "    ");
		if(checkBackSlash){
			elementValue = elementValue.replace(/[\\]/g, "");
		}
		//Updated in R5 removed trim from elementValue
		if(typeof returnFlag != "undefined" && returnFlag != null && returnFlag){
			return elementValue;
		}else if(elementValue != $("#"+elementId).val()){//Updated for Emergency build : 4.0.0.2 defect 8382
			$("#"+elementId).val(elementValue);
		}
	}
}

/**
 * added to fix Defect#6136 to implement the global change for input fields
 * */
function replaceAllExceptAllowedChar(elt){
	var currentLength = $(elt).val().length;
	var lengthAfterReplace = $(elt).val().replace(/[^\u00A7|a-zA-Z0-9!@#\$%\^\&*\)\(+=._{}\[\];:'"<>,?/\|~` -]/g, "").length;
	if(currentLength != lengthAfterReplace)
		$(elt).val($(elt).val().replace(/[^\u00A7|a-zA-Z0-9!@#\$%\^\&*\)\(+=._{}\[\];:'"<>,?/\|~` -]/g, ""));
}
/**
 * Added for Release 5
 * This method is added to create Node of a tree.
 */
function createNode(divId, json,Id)
{
	$js('#'+divId).jstree().create_node(null , json,'','',false);	
}
var fromSelectNode=true;
/**
 * This method is added to show tree based on the user organizataion type.
 * */
function tree(url, divId, variableId, defaultOpenId, OrgId, manageOrgFlag)
{
	var url = url+"&divId="+divId+"&orgId="+OrgId;
	var folderImage = null;
	if((null != divId && divId != '' && divId == 'selectOrgnization') || (typeof loggedInUserOrgType != "undefined" && (loggedInUserOrgType == 'agency_org' || loggedInUserOrgType == 'provider_org') && $("#manageOrganization").length > 0))
	{
		folderImage = "../framework/skins/hhsa/images/select_org_icon.png";
	}
	else
	{
		folderImage = "../framework/skins/hhsa/images/folder-icon_blue.png";
	}
	//Removes jstree cached state from localStorage
	if(null != localStorage && typeof localStorage != "undefined")
		localStorage.removeItem('jstree');
	$js('#'+divId).jstree({
		'core' : {
			'data' : {
				"url" : url,
				"dataType" : "json",
				'cache':false
			},
				 "check_callback" : true,
				 "themes": {
				 "variant" : "large",
				 "dots": false
			}
		},
		"types" : {
			"folder" : {
				"icon" : folderImage
			},
			"file" : {
				"icon" : "../framework/skins/hhsa/images/file.png"
			},
			"recycleBin" : {
				"icon" : "../framework/skins/hhsa/images/recyclebin.png"
			}
		},
		'sort' :  function (a, b) {
			if(this.get_node(b).text.toLowerCase() == 'recycle bin')
			{
			return -1;
			}
			else
			{
			return this.get_node(a).text.toLowerCase() > this.get_node(b).text.toLowerCase() ? 1 : -1;
			}
		 },
		"plugins" : ["types","wholerow","themes","ui","sort"]
	}).on("loaded.jstree", function(e, data) {
		removePageGreyOut();
		if(divId == 'leftTree'){
			 $(".leftTreeWrapper1").scroll(function(){
				 $(".leftTreeWrapper2").scrollLeft($(".leftTreeWrapper1").scrollLeft());
			 });
			 $(".leftTreeWrapper2").scroll(function(){
				 $(".leftTreeWrapper1").scrollLeft($(".leftTreeWrapper2").scrollLeft());
			 });
		}
		if(divId != 'moveTree' && defaultOpenId==="DocumentVault")
		{
		   $js(this).jstree("select_node", $(".jstree-anchor").attr("id")); 
		   $js(this).jstree("open_node", $(".jstree-anchor").attr("id")); 
		}
		else if(defaultOpenId != '')
		{
			 $js(this).jstree("select_node", defaultOpenId);
			 $js(this).jstree("open_node", defaultOpenId);
		}
		//This method is added for defect 8367
		treeTopScroll();
		//This method is added for defect 8367
		fromSelectNode = true;
		$js('#'+divId).on("select_node.jstree", function (evnt, data) {
			$("#findOrgDoc").removeClass("selected");
			var divid = $(evnt.target).closest("div").attr("id");
			$('#selectedTree').val(divid);

			if(fromSelectNode){
				if((divid == 'selectOrgnization' || divid == 'leftTree' ) && manageOrgFlag != '')
				{
					pageGreyOut();
					fromSelectNode = false;
					openFolder(data.node.id,data.node.text,divId,OrgId,data.node.parent,fromSelectNode,data.node.data.organizationId,data.node.data.organizationType);
				}
				else if(divid == 'leftTree' || divid == 'leftTreeVault' && data.node !=undefined )
				{
					pageGreyOut();
					openFolder(data.node.id,data.node.text,divId,OrgId,data.node.parent,fromSelectNode);
				}
			}
			fromSelectNode = !fromSelectNode;
		});
		if(manageOrgFlag != '' && divId == 'selectOrgnization'){
			var json_data = $js("#selectOrgnization").jstree(true).get_json();
			$(json_data).each(function(){
				if(this.data.organizationId == OrgId.split("~")[0]){
					defaultOpenId = this.id;
					$js(this).jstree("select_node", defaultOpenId);
				}
			});
		}
	}).on("changed.jstree", function (evnt, data) {
		var hoveridlen= $('#leftTree ul li').find('.jstree-wholerow-clicked').length;
		if(hoveridlen > 1){
			for(var x=hoveridlen-2;x>=0;x--)
		    {
				$('#leftTree ul li').find('.jstree-wholerow-clicked').eq(x).removeClass('jstree-wholerow-clicked');
		    }
		}
        $("#searchDoc,#recyclebindiv").hide();
		if(null != variableId)
		{
			if(null != $('#'+variableId) && $('#'+variableId) != undefined)
			{
				if(typeof data.node != "undefined" && typeof data.node.id != "undefined")
				{
					document.getElementById(variableId).value = data.node.id;
				}
			}
		}
		var divid = $(evnt.target).closest("div").attr("id");
		if(divid == 'leftTree' || divid == 'leftTreeVault')
		{
			if(typeof data.node != "undefined" && typeof data.node.id != "undefined")
			{
				document.getElementById("presentFolderId").value = data.node.id;
			}
		}
	}).on("open_node.jstree", function (e, data){
		var divid = $(e.target).closest("div").attr("id");
		if(divid == 'selectOrgnization' && data.node.parent == '#')
		{
			$('#OrgNameHeader').show();
			if(data.node.text.length > 25)
			{
				$('#OrgNameHeader').text(data.node.text.substring(0, 25)+'...');
			}
			else
			{
				$('#OrgNameHeader').text(data.node.text);
			}
		}
		var x = data.node.id;
		// Add hidden param valyes
		var _immediateChild=$('#immediateParent').val(x);
		var flag=false;		
		var _selectedId=$('#childToMove').val();
		if(_selectedId === x){
			flag=true;
			$('#flag').val(flag);
		}
	}).on("hover_node.jstree", function(e, data)
	  {
		var id = $js('.jstree-hovered').text();
		if(e.target.id == 'leftTree' && id == 'Recycle Bin')
		{
			$js('.jstree-hovered').prop("title","Contains your organization\'s deleted documents");
		}
		else
		{
			$js('.jstree-hovered').prop("title",$js('.jstree-hovered').text());
		}
	
		
	}).on("select_node.jstree", function (e, data){
		 if(data.instance.get_parent(data.node).length) { 
			   $js(this).jstree("open_node",data.instance.get_parent(data.node)); 
			  } 
		$("#findOrgDoc").removeClass("selected");
		$('#selectedTree').val(e.target.id);
		var id = data.node.id;
		if(fromSelectNode)
		{
			if (id.indexOf("RecycleBin") >= 0)
			{
				$("#vaultheader,#emptyBin").show();
				$("#findOrgDocbtn,#findDoc,#uploadDoc,#newfolder").hide();
			}else{
				$("#findOrgDocbtn,#findDoc,#emptyBin").hide();
				$("#vaultheader,#newfolder,#uploadDoc").show();
			}
		}
		var atLeastOneIsChecked = $("input.isChecked:checked").length > 0;
		if(atLeastOneIsChecked)
		{
			$("#file_menu").css({opacity:0.5});
		}
		var divid = $(e.target).closest("div").attr("id");
		if(divid == 'leftTree')
		{
			$("#toSelectFolderId").val(data.node.id);
		}
	}).on("after_close.jstree", function (e, data){
	//This method is added for defect 8367
		treeTopScroll();
	//This method is added for defect 8367	
	}).on("after_open.jstree", function (e, data){
	//This method is added for defect 8367
		treeTopScroll();
	//This method is added for defect 8367	
	});
};

var options = 
{
	success: function(responseText, statusText, xhr ) 
	{
		removePageGreyOut();
	},
	error:function (xhr, ajaxOptions, thrownError)
	{                     
		showErrorMessagePopup();
		removePageGreyOut();
	}
};

/**
 * R5 changes starts
 * This method is added to enable Dictionary data validation.
 */
function enableDictionary()
{
	if($('textarea').size() > 0)
	{		
		$.get('/HHSPortal/dictionaryAlgoJS', function ( hddnDictionaryAlgo ) {
			$.get('/HHSPortal/dictionaryDataJS', function ( hddnDictionaryData ) {		
				if(typeof hddnDictionaryData === 'undefined' || hddnDictionaryData.trim() == "null" || hddnDictionaryData == null || hddnDictionaryData.trim() == "")
				{
					hddnDictionaryData=(addWordToDictionary(null,null));
				}			
				dictionary = new Typo("en_US", hddnDictionaryAlgo, hddnDictionaryData);
				});
		});
		$("textarea").each(function(){
			var id =  $(this).attr("id");
			var name = $(this).attr("name");
			var maxlength = $(this).attr('maxlength');
			var display = $(this).css('display');
			var disabled = $(this).attr('disabled');
			var readOnly = $(this).attr('readOnly');
			var onChangeProp = $(this).attr('onchange');
			if(display != 'none' && (typeof disabled == 'undefined' || disabled != 'disabled') && (typeof readOnly == 'undefined' || readOnly != 'readonly') && !$(this).hasClass("hwt-input"))
			{
				var _heigth = $("#"+id).innerHeight();
				_heigth = _heigth + 26;
				if(_heigth == null)
				{
					_heigth = 0;
				}
				var _width = $("#"+id).innerWidth();
				_width = _width - 10;
				if(_width == null)
				{
					_width = 0;
				}
				var _maxlength = "";
				var _divCount = "";
				if(typeof maxlength != 'undefined' && maxlength != null && maxlength != '' && maxlength != 'null')
				{
					maxlength = maxlength.replace('px','');
					_maxlength = maxlength;
					_divCount = maxlength;
				}
				else if($(this).attr('onkeyup') != "undefined" || $(this).attr('onkeypress') != "undefined")
				{
					var tmp = "";
					if($(this).attr('onkeyup') != ""){
						tmp = $(this).attr('onkeyup').replace(/\s/g,'').replace(';','');
					}
					else if($(this).attr('onkeypress') != ""){
						tmp = $(this).attr('onkeypress').replace(/\s/g,'').replace(';','');
					}
					_divCount = tmp.substring(tmp.lastIndexOf(',')+1, tmp.length-1);
					if(tmp != ""){
						_maxlength = _divCount;
					}
				}
				_divCount = _divCount - $.trim($(this).val()).length;
				var $characterLeft = "";
				if(!isNaN(_divCount))
				{
					$characterLeft = $characterLeft + "<span id='"+id+"_count' style='font-weight:bold;'>"+_divCount+"</span> characters left</div>";
				}
				else if(!isNaN($(this).parent().attr('maxlength')))
				{
					_divCount = $(this).parent().attr('maxlength');
					_maxlength = _divCount;
					_divCount = _divCount - $.trim($(this).val()).length;
					$characterLeft = $characterLeft + "<span id='"+id+"_count' style='font-weight:bold;'>"+_divCount+"</span> characters left</div>";
				}
				var query = "<div class='textareaDiv' style='position:relative;height:"+_heigth+"px; width:"+_width+"px;' id='"+id+"_container'><img src='../framework/skins/hhsa/images/ProcessingSmallIcon.gif' style='position: absolute; top: 0px; right: 18px; display: none;'/><div class='dataDictionarySuggestions' style='display:none'><ul></ul></div><div style = 'position: absolute;right: 20px;'>"+$characterLeft+"</div>";
				$("#"+id).attr("spellcheck", false).attr("maxlength", _maxlength);
				$("#"+id).removeAttr('rows');
				$("#"+id).attr("style", (typeof($("#"+id).attr("style"))!="undefined"?$("#"+id).attr("style")+";":"") + "height:"+(_heigth - 20)+"px !important;"+"width:"+(_width - 20)+"px !important;");
				$(query).insertAfter("#"+id);
				$("#"+id+"_container").prepend($("#"+id));
				addHighlightOnTextarea($("#"+id));
				$("#"+id).closest(".row").addClass('rowtextarea');
				$(".textareaDiv").on("contextmenu", function(){
					var count=0;
					$(this).find("mark").each(function(){
						if($(this).text().length > 0){
							count++;
						}
					})
					if(count > 0)
						return false;
				})
			}
		});
		
		$(".textareaDiv").mousedown(function(e){
			if( e.button == 2 && e.target.nodeName.toLowerCase() == 'textarea'){
			  $(".hwt-backdrop").css("z-index", 10000);
			}
		  }); 
		$(".textareaDiv").mouseup(function(e){
			$(".hwt-backdrop").css("z-index", "auto");
			if( e.button == 2 && e.target.nodeName.toLowerCase() == 'mark') {
			  showCoords(e);
			  return false; 
			}
			return true; 
		});
	}
}
/**
 * This method is added to provide data Dictionary Suggestions.
 * */
function showCoords(event) {
	var currentElt = event.target;
	var word = $(currentElt).text();
	if(word != '') {
	  var $suggestionBox = $(currentElt).closest(".textareaDiv").find(".dataDictionarySuggestions");
	  var array_of_suggestions = dictionary.suggest(word, 4);/*Maximum suggest word are 4*/
	  $('.dataDictionarySuggestions').hide();
       	$('.dataDictionarySuggestions').empty();
       	$suggestionBox.append('<ul>');
		for(var i = 0; i < array_of_suggestions.length; i++) {
			$suggestionBox.find("ul").append('<li><a href="javascript:void(0)">'+array_of_suggestions[i].trim()+'</a></li>');
		}
		if(array_of_suggestions.length == 0){
			$suggestionBox.find("ul").append('<li><span>(No Suggestions)</span></li>');
		}
		if($('#typeOfUser').val() == 'city_org')
		{
			$suggestionBox.find("ul").append('<li><a href="javascript:void(0)" onClick="addWordToDictionary(\''+word+'\',\''+currentElt.offsetParent.firstChild.id+'\');">Add To Dictionary</a></li>');
		}
		$suggestionBox.append('</ul>');
		if(array_of_suggestions.length != 0)
       	{
			$suggestionBox.show();
       	}
		$suggestionBox.find("a").bind("click", function()
		{
			$(window).scrollTop($(window).scrollTop());
			var elt = $(this).closest(".textareaDiv").find("textarea");
			var _text = $(this).text();
			if(_text == 'Add To Dictionary')
			{
				_text = word;
				$(elt).val($(elt).val().replace(new RegExp('\\b' + word.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&") + '\\b', 'g'), _text));
			}else{
				$(currentElt).text(_text);
				$(elt).val($(this).closest(".textareaDiv").find(".hwt-backdrop").text());
			}
			setMaxLengthForAddContract(elt, elt.attr("maxlength"));
			addHighlightOnTextarea(elt);
			 $(elt).focus();
			 $('.dataDictionarySuggestions').empty();
			 $('.dataDictionarySuggestions').hide();
		});
	}
	menuPosition= setContextMenuPostion(event, $suggestionBox);
	$suggestionBox.css({"display":"block","position":"fixed","left":menuPosition.x+'px',"top":menuPosition.y+'px','text-decoration':'underline','color':'#5077AA !important'});
}

if (window.getSelection && document.createRange) {
    saveSelection = function(containerEl) {
        var range = window.getSelection().getRangeAt(0);
        var preSelectionRange = range.cloneRange();
        preSelectionRange.selectNodeContents(containerEl);
        preSelectionRange.setEnd(range.startContainer, range.startOffset);
        var start = preSelectionRange.toString().length;

        return {
            start: start,
            end: start + range.toString().length
        }
    };

    restoreSelection = function(containerEl, savedSel) {
        var charIndex = 0, range = document.createRange();
        range.setStart(containerEl, 0);
        range.collapse(true);
        var nodeStack = [containerEl], node, foundStart = false, stop = false;
        
        while (!stop && (node = nodeStack.pop())) {
            if (node.nodeType == 3) {
                var nextCharIndex = charIndex + node.length;
                if (!foundStart && savedSel.start >= charIndex && savedSel.start <= nextCharIndex) {
                    range.setStart(node, savedSel.start - charIndex);
                    foundStart = true;
                }
                if (foundStart && savedSel.end >= charIndex && savedSel.end <= nextCharIndex) {
                    range.setEnd(node, savedSel.end - charIndex);
                    stop = true;
                }
                charIndex = nextCharIndex;
            } else {
                var i = node.childNodes.length;
                while (i--) {
                    nodeStack.push(node.childNodes[i]);
                }
            }
        }
        var sel = window.getSelection();
        sel.removeAllRanges();
        sel.addRange(range);
    }
} else if (document.selection && document.body.createTextRange) {
    saveSelection = function(containerEl) {
        var selectedTextRange = document.selection.createRange();
        var preSelectionTextRange = document.body.createTextRange();
        preSelectionTextRange.moveToElementText(containerEl);
        preSelectionTextRange.setEndPoint("EndToStart", selectedTextRange);
        var start = preSelectionTextRange.text.length;

        return {
            start: start,
            end: start + selectedTextRange.text.length
        }
    };

    restoreSelection = function(containerEl, savedSel) {
        var textRange = document.body.createTextRange();
        textRange.moveToElementText(containerEl);
        textRange.collapse(true);
        textRange.moveEnd("character", savedSel.end);
        textRange.moveStart("character", savedSel.start);
        textRange.select();
    };
}

/**
 * This method is added to set Postion of Context Menu.
 * */
function setContextMenuPostion(event, contextMenu) {
    var mousePosition = {};
    var menuPostion = {};
    var menuDimension = {};

    menuDimension.x = contextMenu.outerWidth();
    menuDimension.y = contextMenu.outerHeight();
    mousePosition.x = event.pageX;
    mousePosition.y = event.pageY;

    if (mousePosition.x + menuDimension.x > $(window).width() + $(window).scrollLeft()) {
        menuPostion.x = mousePosition.x - menuDimension.x - $(window).scrollLeft();
    } else {
        menuPostion.x = mousePosition.x - $(window).scrollLeft();
    }
    
    if (mousePosition.y + menuDimension.y > $(window).height() + $(window).scrollTop() - 40) {
        menuPostion.y = mousePosition.y - menuDimension.y - $(window).scrollTop();
    } else {
        menuPostion.y = mousePosition.y - $(window).scrollTop();    
    }
    if(menuPostion.x < 0){
      menuPostion.x = event.offsetX;
      if(menuPostion.y < 0) {
            menuPostion.y = event.offsetY;
      }
    }
    return menuPostion;
}
/**
 * This method is added to get pre Caret Text Range.
 * */
function getCaret(element) {
	var start = 0;
    var end = 0;
    var doc = element.ownerDocument || element.document;
    var win = doc.defaultView || doc.parentWindow;
    var sel;
    if (typeof win.getSelection != "undefined") {
        sel = win.getSelection();
        if (sel.rangeCount > 0) {
            var range = win.getSelection().getRangeAt(0);
            var preCaretRange = range.cloneRange();
            preCaretRange.selectNodeContents(element);
            preCaretRange.setEnd(range.startContainer, range.startOffset);
            start = preCaretRange.toString().length;
            preCaretRange.setEnd(range.endContainer, range.endOffset);
            end = preCaretRange.toString().length;
        }
    } else if ( (sel = doc.selection) && sel.type != "Control") {
        var textRange = sel.createRange();
        var preCaretTextRange = doc.body.createTextRange();
        preCaretTextRange.moveToElementText(element);
        preCaretTextRange.setEndPoint("EndToStart", textRange);
        end = preCaretTextRange.text.length;
        preCaretTextRange.setEndPoint("EndToEnd", textRange);
        end = preCaretTextRange.text.length;
    }
    return { start: start, end: end};
}
/**
 * This method is added to add a word which is not present in the dictionary.
 * */
function addWordToDictionary(word, textAreaId){
	pageGreyOut();
	var _wordData = null;
	var _url = "/HHSPortal/GetContent.jsp?alertAction=dictionaryData";
	if(word != null)
	{
		_url = _url + "&addWordToDictionary="+word;
	}
	jQuery.ajax({
		type : "POST",
		url : _url,
		data : "",
		contentType:"application/json; charset=utf-8",
	    dataType: "html",
		success : function(e) {
			dictionary = new Typo("en_US", hddnDictionaryAlgo, e);
			hddnDictionaryData=e;		
			_wordData = e;
			addHighlightOnTextarea($(".hwt-input"));
			removePageGreyOut();
		},
		error: function() {
			removePageGreyOut();
			showErrorMessagePopup();
		}
	  });
	return _wordData;
}
$.fn.getDivContent = function(){
	return $(this).contents().filter(function() {
	    return this.nodeType == 3;
	}).text();
}
/*Start : Added for spell check*/
var isOpera = (!!window.opr && !!opr.addons) || !!window.opera || navigator.userAgent.indexOf(' OPR/') >= 0;
//Firefox 1.0+
var isFirefox = typeof InstallTrigger !== 'undefined';
//At least Safari 3+: "[object HTMLElementConstructor]"
var isSafari = Object.prototype.toString.call(window.HTMLElement).indexOf('Constructor') > 0;
//Internet Explorer 6-11
var isIE = /*@cc_on!@*/false || !!document.documentMode;
//Edge 20+
var isEdge = !isIE && !!window.StyleMedia;
//Chrome 1+
var isChrome = !!window.chrome && !!window.chrome.webstore;
//Blink engine detection
var isBlink = (isChrome || isOpera) && !!window.CSS;
/*End : Added for spell check*/
//R5 changes ends
/**
 * This method is added to enable Auto Save data .
 * */
function enableAutoSave(){
	if($('textarea').size() > 0)
	{
		$("textarea").each(function(){
			textareaDataArray[$(this).attr("name")] = $(this).val();
		});
		
		populateAutoSavedData();
		
		setInterval(function() {
			var dataToSend = [];
			var imgObj = [];
			$("textarea").each(function(){
				if(textareaDataArray[$(this).attr("name")] != $(this).val()){
					var data = {};
					data.name=$(this).attr("name");
					data.value=$(this).val();
					dataToSend.push(data);
					imgObj.push($(this).closest('.textareaDiv').find("img"));
					textareaDataArray[$(this).attr("name")] = $(this).val();
				}
			});
			if(dataToSend.length > 0)
			{
				$(imgObj).each(function(){
		    		$(this).show();
		    	});
				var urlAppender = $("#contextPathSession").val()+ '/AutoSave?autoSaveAction=updateAutoSaveData';
				jQuery.ajax({
					type: "POST",
				    url: urlAppender,
				    data : dataToSend,
				    success: function(e)
				    {
				    	//Start : Added for Defect-8472
				    	$.each(autoSaveDataTempArray, function(i, element){
				    		if(element.value!=textareaDataArray[element.name] && textareaDataArray[element.name]!=null){
                              element.value=textareaDataArray[element.name];
                            }
				    	});
				    	//End : Added for Defect-8472
				    	$(imgObj).each(function(){
				    		$(this).hide();
				    	});
				    }
				});
			}
		}, 10000);
	}
}
/**
 * This method is added to populate Auto Saved Data in text area when screen is rendered again.
 **/
function populateAutoSavedData(){
	$.each(autoSaveDataTempArray, function(i, element){
		var localElt = $("textarea[name='"+element.name+"']");
		if(localElt.attr("autosavepopulated") == null 
				|| typeof localElt.attr("autosavepopulated") == "undefined"){
			var display = localElt.css('display');
			var disabled = localElt.attr('disabled');
			var readOnly = localElt.attr('readOnly');
			if(display != 'none' && (typeof disabled == 'undefined' || disabled != 'disabled') && (typeof readOnly == 'undefined' || readOnly != 'readonly'))
			{
				localElt.html(element.value);
				// Updated for Emergency build : 4.0.0.2 defect 8383
				localElt.attr("onfocus", "return true;");
				localElt.attr("autosavepopulated", '1');
			}
		}
	});
}
/**
 * This method is added to export Task List .
 * */
function exportTaskList1()
{
	// Emergency build changes Start: 4.0.0.2 defect 8379
	var selectedAgency= $('#agencySelectBoxExport').val();
	// Emergency build changes End
	if(null != selectedAgency && selectedAgency != 'undefined' &&  typeof(selectedAgency) != 'undefined')
		{
		window.open($("#contextPathVal").val()+'/GetContent.jsp?isExportList=true&selectedAgency='+selectedAgency);
		}
	else
		{
		window.open($("#contextPathVal").val()+'/GetContent.jsp?isExportList=true&selectedAgency=');
		}
}
/**
 * This method is added to export Task List.
 **/
function exportTaskList(){
	pageGreyOut();
	// Emergency build changes Start: 4.0.0.2 defect 8379
	var v_parameter = "&selectedAgency=" + $('#agencySelectBoxExport').val();
	// Emergency build changes End
	var urlAppender = $("#exportAllTask").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(responseText) {
			$("#transactionStatusDiv").show();
			$("#transactionStatusDiv").html("Your export request is in progress. You will receive an email when the file is ready for download.");
			removePageGreyOut();
			$(".overlay").closeOverlay();
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			 removePageGreyOut();
		}
	});
}

/**
 * This method is added to export Task List for Agency
 **/
function exportTaskListAgency(agencyName){
	pageGreyOut();
	var v_parameter = "&selectedAgency=" + agencyName;
	var urlAppender = $("#exportAllTask").val();
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(responseText) {
			$("#transactionStatusDiv").show();
			$("#transactionStatusDiv").html("Your export request is in progress. You will receive an email when the file is ready for download.");
			removePageGreyOut();
			$(".overlay").closeOverlay();
		},
		error : function(data, textStatus, errorThrown) {
			showErrorMessagePopup();
			 removePageGreyOut();
		}
	});
}
/**
 * This method is added to launch Overlay for export Task List PopUp.
 **/
function exportTaskListPopUp()
{
	$(".overlay").launchOverlay($(".alert-box-exportTaskList"),
		$(".exit-panel, #exportCancelButton"), "500px");
	//defect 8267
	// Emergency build changes Start: 4.0.0.2 defect 8379
	$('#agencySelectBoxExport').val('');	
	// Emergency build changes End
}
/**
 * This method is added for sorting nodes of tree.
 **/
function sortTreeNode(selectedNode){
	$js("#leftTree").jstree(true).sort(selectedNode, true);
	$js("#leftTree").jstree(true).redraw_node(selectedNode, true);
}
/**
 * This method is added to delete Auto Save Data.
 * */
function deleteAutoSaveData(){
	jQuery.ajax({
		type : "POST",
		url : $("#contextPathSession").val()+ '/AutoSave?autoSaveAction=deleteAutoSaveData',
		async:false
	  });
}
/** 
 * This method is added to open Root Node In the Tree.
 **/
function openRootNodeInTree(divId)
{
	var nodeId = $js("#"+divId).jstree("get_selected");
	var nodetoclose = nodeId;
	while($js('#'+divId).jstree(true).get_node(nodeId).parent != "#")
	{
		nodeId = $js('#'+divId).jstree(true).get_node(nodeId).parent;
	}
	if(typeof(nodeId) === 'string'){
		$("#" + nodeId.replace(/{/g, '\\{').replace(/}/g, '\\}')+"_anchor").eq(0).parent().find("div")[0].click();
	}
	else{
		$("#" + nodeId[0].replace(/{/g, '\\{').replace(/}/g, '\\}')+"_anchor").eq(0).parent().find("div")[0].click();
	}
	$js("#"+divId).jstree("close_node",nodetoclose);
}

/**
 * This method returns the length of the innerText 
 * plus the number of blank lines in the text 
 * which are not added by default in the innerText.length value 
 * 
 * @param obj the textarea dom object
 * @returns length of the innerText 
 * plus the number of blank lines
 */
function getTextLengthWithBlankLines(obj){
	var characters = document.getElementById(obj.id).innerText.length;;
	var newlines = (document.getElementById(obj.id).innerHTML.match(/<br>/g)||[]).length;
	var blanklines = (document.getElementById(obj.id).innerHTML.match(/<br>\n/g)||[]).length;
	
	if ((characters>0) && (characters == newlines)) characters -= 1;
	
	if (!!newlines) newlines -= 1;
	characters = characters + newlines - blanklines;
	return characters;
}



/**
 * document.oncontextmenu = function() {return false;};
 * This method is added to calculate the number of characters that can be added in text area.
 */
function onInputRegex(input, $element) {
	var elementId = $element.attr("id");
	var maxLength = $element.attr("maxlength");
	setMaxLength($element, maxLength);
	convertSpecialCharactersHTMLGlobal(elementId, true);
	var res = $element.val().split(/[\s,!.?\n]+/);
	res = $.unique(res);
	var regex = "";
	var remainingCharacters = maxLength - ($element.val().length + $element.val().split("\n").length-1);
	$("#"+elementId+"_count").text(remainingCharacters);
	if(res.length >= 1)
	{
		for (i = 0; i < res.length; ++i) 
		{
			try {
				var $wordToTest = res[i].trim();
				
				if( typeof $wordToTest != 'undefined' || $wordToTest != '' && !dictionary.check($wordToTest))
				{
					regex += '\\b' + $wordToTest.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&") + '\\b|';
				}
			}catch (error){
				console.error(error);
			}
		}
	}
	if(regex.length > 0)
		regex = regex.substr(0, regex.length-1);
	return new RegExp(regex, "gi");
}

/**
 * This method adds highlight functionality on textarea on wrong word
 **/
function addHighlightOnTextarea($element){
	$element.highlightWithinTextarea(onInputRegex);
	$element.each(function(){
		$(this).parent().find('.hwt-backdrop').css("width",$(this).css("width"));
	})	
}
//This method is added for defect 8367
function treeTopScroll(){
	var maxWidth=0;
	$(".leftTreeScroll2>ul").each(function(){
		if(maxWidth < $(this).width()){
			maxWidth= $(this).width();
		}
	})
	$(".leftTreeScroll1").width(maxWidth);
}