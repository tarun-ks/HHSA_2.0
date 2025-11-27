//Method for rendering a page.
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
				//removesBtWrappers();
				//hhsAjaxBeforeSend(hhsAjaxMode);
			},
			success : function(data) {
				hhsAjaxCallback(data, hhsAjaxMode, callbackParams);
				callBackInWindow(callbackMethodName);
			},
			error : function(data, textStatus, errorThrown) {
				//hhsAjaxError(data, textStatus, errorThrown);
			},
			complete : function() {
				//hhsAjaxOnComplete(namespace, hhsAjaxMode);
			}
		});

		//ajaxDynamicFieldDiv.innerHTML = "";
		
	}
/*
 ** retrieve clicked object 
 */
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
//Method to create ajax input.
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
}
//Method to refersh element.
function refreshElementById(id, responseDOM, jQueryableFormId) {
	var idToRefresh = jQueryableFormId;
	var foundId = id;
	if (foundId != null)
		idToRefresh = foundId;
	var objToRefresh = $(document).find('#' + idToRefresh);

	var objRefreshed = responseDOM.find('#' + idToRefresh);
	
	if((objToRefresh.length == 0) || (objRefreshed.length == 0)){
		throw ("ElementNotFound: id = " + idToRefresh);
	}
	try{
		objToRefresh.html(objRefreshed.html());
	} catch(e){
		var idToRefreshJavascript = idToRefresh.replace(/\\:/g,':');
		document.getElementById(idToRefreshJavascript).innerHTML = objRefreshed.html();
	}
}

//Method hhs Ajax Call back.
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
}

