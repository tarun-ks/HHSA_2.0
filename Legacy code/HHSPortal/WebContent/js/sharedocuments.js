/**
 * ==========================================
 * This file contains method that will
 * handle functionality when a document
 * is shared.
 * ==========================================
 */
/**
 *  This will execute when Cancel button is clicked from Share document step1 screen
 **/
$(".alert-box-sharedoc").find('#cancelshare1').click(function() {
	$(".overlay").closeOverlay();
	return false;
});

/**
 *  This will execute when Next button is clicked from Share document step 1 screen
 **/
function shareScreen1(providerName) {
	document.getElementById("proNameString").value = providerName;
}

/**
 *  This will execute when Next button is clicked from Share document Step 2
 **/
$(".alert-box-sharedoc").find('#nextshare2').click(function() { 
		// Start Updated in R5
			document.share2.action = $('#fromStep3SharingForm').val();
			shareScreen2(this.form);
			var options = {
				success : function(responseText, statusText, xhr) {
					$("#tab5").empty();
					$("#tab3").empty();
					$("#tab4").empty();
					$("#tab6").empty();
					$("#tab6").html(responseText);
					$("#sharelabel").html("- Step 4");
					callBackInWindow("onReady");
					$('#sharewiz').removeClass('wizardUlStep1 wizardUlStep2 wizardUlStep3')
							.addClass('wizardUlStep4');
					removePageGreyOut();
				},
				error : function(xhr, ajaxOptions, thrownError) {
					showErrorMessagePopup();
			// End Updated in R5
				}
			};
			$(this.form).ajaxSubmit(options);
			return false;
		});

/**
 *  This will execute when Cancel button is clicked from Share document step2 screen
 */
$(".alert-box-sharedoc").find('#cancelshare2').click(function() {
	$(".overlay").closeOverlay();
	$(".autocomplete-w1").hide();
	return false;
});
$(".alert-box-sharedoc").find('#exitShare').click(function() {
	$(".autocomplete-w1").hide();
});

/**
 *  This will execute when Back button is clicked from Share document step2 screen
 **/
$(".alert-box-sharedoc").find('#backshare2').click(
		
		function() { 
			// Start Updated in R5
			document.share3.action = $('#backtoStep1SharingForm').val();
			// End Updated in R5
			backtoStep1(this.form);
			var options = {
				success : function(responseText, statusText, xhr) {
					$("#tab3").empty();
					$("#tab4").empty();
					$("#tab5").empty();
					$("#tab6").empty();
						$("#tab3").html(responseText);
					$("#sharelabel").html("- Step 1");
					callBackInWindow("onReady");
					$('#sharewiz').removeClass(
							'wizardUlStep2 wizardUlStep3 wizardUlStep4')
							.addClass('wizardUlStep1');
					removePageGreyOut();
				},
				error : function(xhr, ajaxOptions, thrownError) {
					showErrorMessagePopup();
					// Start Updated in R5
					// End Updated in R5
				}
			};
			$(this.form).ajaxSubmit(options);
			return false;
		});

/**
 *  This will execute when Next button is clicked from Share document step2 screen
 **/
function shareScreen2(form) {
	var table = document.getElementById("mytable");
	var proName = "";
	var rowCount = table.rows.length;
	for ( var i = 0; i < rowCount; i++) {
		var rowobj = table.rows[i];
		var text = rowobj.cells;
		if (text[2].innerHTML.startsWith("^PROVIDER")) {
			proName = proName + text[0].innerHTML + "^PROVIDER" + "|";
		} else {
			proName = proName + text[0].innerHTML + "^AGENCY" + "|";
		}
	}
	// Start Updated in R5
	// End Updated in R5
	document.getElementById("proNameString").value=proName;
}

/**
 *  This will execute when Back button is clicked from Share document step2 screen
 **/
function backtoStep1(form) {
	// Start Updated in R5
	var table = document.getElementById("mytable2");
	// End Updated in R5
	var proName = "";
	var rowCount = table.rows.length;
	for ( var i = 0; i < rowCount; i++) {
		var rowobj = table.rows[i];
		var text = rowobj.cells;
		if (text[2].innerHTML.startsWith("^PROVIDER")) {
			proName = proName + text[0].innerHTML + "^PROVIDER" + "|";
		} else {
			proName = proName + text[0].innerHTML + "^AGENCY" + "|";
		}
	}
	// Start Updated in R5
	// End Updated in R5
	document.getElementById("proNameString").value=proName;
}

/**
 *  This will execute when any provider from type head search is selected or when user clicks back from screen 3
 **/
function populateTable(provName, agencyType) {
	var providerName = "";
	if ('null' != provName && "" != provName) {
		var provNames = provName.split("|");
		for ( var i = 0; i < provNames.length - 1; i++) {
			var agencyName = provNames[i].substring(0, provNames[i]
					.indexOf("^"));
			if (checkNameAlreadyExist(agencyName, "mytable")) {
				var agencyType = provNames[i].substring(provNames[i]
						.indexOf("^"), provNames[i].length);
				var table = document.getElementById("mytable");
				var rowCount = table.rows.length;
				var row = table.insertRow(rowCount);
				row.setAttribute("id", rowCount);
				var cell1 = row.insertCell(0);
				var cell2 = row.insertCell(1);
				var cell3 = row.insertCell(2);
				$(cell1).append(agencyName);
				var link = document.createElement("a");
				link.setAttribute("href", "javascript:deleteRow('" + rowCount
						+ "', '#mytable tr')");
				var linkText = document.createTextNode("Remove");
				link.appendChild(linkText);
				cell2.appendChild(link);
				var hiddenText = document.createTextNode(agencyType);
				cell3.appendChild(hiddenText);
				cell3.style.display = 'none';
				cell3.style.width = '0px';
			}
		}
		applyCssToTable("#mytable tr");
	} else if ("" != document.getElementById("provName").value) {
		providerName = document.getElementById("provName").value;
		if (checkNameAlreadyExist(providerName, "mytable")) {
			var table = document.getElementById("mytable");
			var rowCount = table.rows.length;
			var row = table.insertRow(rowCount);
			row.setAttribute("id", rowCount);
			var cell1 = row.insertCell(0);
			var cell2 = row.insertCell(1);
			var cell3 = row.insertCell(2);
			cell1.appendChild(document.createTextNode(providerName));
			var link = document.createElement("a");
			link.setAttribute("href", "javascript:deleteRow('" + rowCount
					+ "', '#mytable tr')");
			var linkText = document.createTextNode("Remove");
			link.appendChild(linkText);
			cell2.appendChild(link);
			var hiddenText = document.createTextNode(agencyType);
			cell3.appendChild(hiddenText);
			cell3.style.display = 'none';
			cell3.style.width = '0px';
			document.getElementById("provName").value = "";
		}
		applyCssToTable("#mytable tr");
	}
}

/**
 *  This will execute during type head search for provider
 **/
function isAutoSuggestValid(providersName, suggestionVal) {
	var uoValid = false;
	if (suggestionVal.length > 0) {
		for (i = 0; i < suggestionVal.length; i++) {
			var arrVal = suggestionVal[i].toUpperCase();
			if (arrVal == providersName.toUpperCase()) {
				uoValid = true;
				break;
			}
		}
	}
	return uoValid;
}

/**
 *  This will execute when Next button is clicked from Share document screen 3
 **/
$('#nextshare3').click(function() { 
	
			pageGreyOut();
			document.share3.action = $('#fromStep2SharingForm').val();
			
			shareScreen3(this.form);
			var options = {
				success : function(responseText, statusText, xhr) {
					$("#tab3").empty();
					$("#tab4").empty();
					$("#tab5").empty();
					$("#tab6").empty();
					// Start Updated in R5
					$("#tab5").html(responseText);
					$("#sharelabel").html("- Step 3");
					
					callBackInWindow("onReady");
					$('#sharewiz').removeClass(
							'wizardUlStep1 wizardUlStep2 ')
							.addClass('wizardUlStep3');
					// End Updated in R5
					removePageGreyOut();
				},
				error : function(xhr, ajaxOptions, thrownError) {
					showErrorMessagePopup();
					removePageGreyOut();
				}
			};
			$(this.form).ajaxSubmit(options);
			return false;
		});

// Sharing Enhanced

$(".alert-box-sharedoc").find('#nextshare1').click(function() {
	// bind click event to link
	pageGreyOut();
	document.share1.action = $('#fromStep1SharingForm').val();
	var options = 
	{
	success: function(responseText, statusText, xhr ) 
	{
        $("#tab3").empty();
		$("#tab4").empty();
		$("#tab5").empty();
		$("#tab6").empty();
        $("#tab4").html(responseText);
		$("#sharelabel").html("- Step 2");
		callBackInWindow("onReady");
		$('#sharewiz').removeClass('wizardUlStep1').addClass('wizardUlStep2');
		$('step2confirmDoc').css("background-color", "#333333");
		$.unblockUI();
		removePageGreyOut();
	},
	error:function (xhr, ajaxOptions, thrownError)
	{                     
		showErrorMessagePopup();
		removePageGreyOut();
	}
};
$(this.form).ajaxSubmit(options);
 return false;
});

//

/**
 *  This will execute when Cancel button is clicked from Share document screen 3
 **/
$('#cancelshare3').click(function() {
	$(".overlay").closeOverlay();
	return false;
});

/**
 *  This will execute when Back button is clicked from Share document screen 3
 **/
$('#backshare3').click(
		function() { 
			pageGreyOut();
			// Start Added in R5
			document.share2.action = $('#backtoStep2SharingForm').val();
			// End Added in R5
		    backtoStep2(this.form);
			var options = {
				success : function(responseText, statusText, xhr) {
					$("#tab3").empty();
					$("#tab4").empty();
					$("#tab5").empty();
					$("#tab6").empty();
					$("#tab4").html(responseText);
					$("#sharelabel").html("- Step 2");
					
					callBackInWindow("onReady");
					$('#sharewiz').removeClass(
							'wizardUlStep1 wizardUlStep3 wizardUlStep4')
							.addClass('wizardUlStep2');
					removePageGreyOut();
				},
				error : function(xhr, ajaxOptions, thrownError) {
					showErrorMessagePopup();
					removePageGreyOut();
				}
			};
			$(this.form).ajaxSubmit(options);
			return false;
		});
/**
 * Added in R5 : This will execute when Back button is clicked from Share Document step4
 **/
$('#backshare4').click(function() { // bind click event to link
	backtoStep3(this.form);
	pageGreyOut();
	var options = 
		{	
		   	success: function(responseText, statusText, xhr ) 
			{
	            $("#tab6").empty();
			 	$("#tab5").empty();
			 	$("#tab4").empty();
			 	$("#tab3").empty();
	            $("#tab5").html(responseText);
				$("#sharelabel").html("- Step 3");
				
				callBackInWindow("onReady");
				$('#sharewiz').removeClass('wizardUlStep1 wizardUlStep2 wizardUlStep4').addClass('wizardUlStep3');	
				$.unblockUI();
				removePageGreyOut();
			},
			error:function (xhr, ajaxOptions, thrownError)
			{                     
				showErrorMessagePopup();
				removePageGreyOut();
			}
		};
	$(this.form).ajaxSubmit(options);
	return false;
});

/**
 *  This will execute when Next button is clicked from Share document screen 3
 **/
function shareScreen3(form) {

	var table = document.getElementById("mytable2");
	var proAndAgencyName = "";
	var rowCount = table.rows.length;
	for ( var i = 0; i < rowCount; i++) {
		var rowobj = table.rows[i];
		var text = rowobj.cells;
		if (text[2].innerHTML.startsWith("^PROVIDER")) {
			proAndAgencyName = proAndAgencyName + text[0].innerHTML
					+ "^PROVIDER|";
		} else {
			proAndAgencyName = proAndAgencyName + text[0].innerHTML
					+ "^AGENCY|";
		}
	}
	// Start Updated in R5
	// End Updated in R5
	document.getElementById("proNameString").value=proAndAgencyName;
}

/**
 *  This will execute when Back button is clicked from Share document screen 2
 **/
function backtoStep2(form) {
	// Start Updated in R5
	var table = document.getElementById("mytable");
	// End Updated in R5
	var proAndAgencyName = "";
	var rowCount = table.rows.length;
	for ( var i = 0; i < rowCount; i++) {
		var rowobj = table.rows[i];
		var text = rowobj.cells;
		if (text[2].innerHTML.startsWith("^PROVIDER")) {
			proAndAgencyName = proAndAgencyName + text[0].innerHTML
					+ "^PROVIDER|";
		} else {
			proAndAgencyName = proAndAgencyName + text[0].innerHTML
					+ "^AGENCY|";
		}
	}
	// Start Updated in R5
	// End Updated in R5
	document.getElementById("proNameString").value=proAndAgencyName;
}


/**
 *  This will execute on load of screen 3 to populate the values of screen 2
 **/
function splitPreviousScreenValues(proNameString) {
	var nameArray = proNameString.split("|");
	var provName = "";
	for ( var i = 0; i < nameArray.length - 1; i++) {
		var provName = nameArray[i].substring(0, nameArray[i].indexOf("^"));
		var provType = nameArray[i].substring(nameArray[i].indexOf("^"),
				nameArray[i].length);
		populatePreviousScreenValues(provName, provType);
	}
}

/**
 * This will execute on load of screen 3 to populate the values of screen 2
 **/
function populatePreviousScreenValues(provName, provType) {
	insertRowInTable(provName, provType);
	applyCssToTable("#mytable2 tr");
}

/**
 *  This will execute to populate the Nyc Agency values select
 **/
function populateNYCAgency(agencySet) {
	if ('null' != agencySet) {
		var agencies = agencySet.substring(1, agencySet.length - 1);
		var agencyArray = agencies.split(",");
		var selectboxagency = document.getElementById("selectagency");

		for ( var j = selectboxagency.options.length - 1; j >= 0; j--) {
			selectboxagency.remove(j);
		}
		var optn = document.createElement("OPTION");
		optn.text = "All NYC Agencies";
		optn.value = "All NYC Agencies";
		selectboxagency.options.add(optn);
		for ( var i = 0; i < agencyArray.length; i++) {
				var optn = document.createElement("OPTION");
				var agencyString = agencyArray[i];
				optn.text = unescape(agencyString.substring(agencyString.indexOf("~")+1,agencyString.length));
				optn.value = agencyString.substring(0, agencyString.indexOf("~"));
				selectboxagency.options.add(optn);
		}
	}
}

/**
 *  This will execute to add selected agency and provider to table
 **/
function insertRowInTable(agencyName, agencyType) {
	if (checkNameAlreadyExist(agencyName, "mytable2")) {
		var table = document.getElementById("mytable2");
		var rowCount = table.rows.length;
		var row = table.insertRow(rowCount);
		row.setAttribute("id", rowCount);
		var cell1 = row.insertCell(0);
		var cell2 = row.insertCell(1);
		var cell3 = row.insertCell(2);
		$(cell1).append(agencyName);
		var link = document.createElement("a");
		link.setAttribute("href", "javascript:deleteRow('" + rowCount
				+ "', '#mytable2 tr')");
		var linkText = document.createTextNode("Remove");
		link.appendChild(linkText);
		cell2.appendChild(link);
		var hiddenText = document.createTextNode(agencyType);
		cell3.appendChild(hiddenText);
		cell3.style.display = 'none';
		cell3.style.width = '0px';
		// Start -  Defect : 6200, 6232, 6231
		var _agencyName = new Array();
		$('#mytable2>tbody>tr').each(function(item) {
			var _tmp = '';
			$(this).find('td').each (function(i) {
				if(i != 1){
					if(_tmp != '' ){
						_tmp = _tmp + '||' + $(this).html();
					}else{
						_tmp =  $(this).html();
					}
				}
			}); 
			_agencyName[item] = _tmp;
			
		});
		_agencyName.sort();
		$('#mytable2>tbody>tr').each(function(item) {
			$(this).find('td').each (function(i) {
				var _tmp = _agencyName[item].split('||');
				if(i == 0){
					$(this).html(_tmp[0]);
				}
				else if(i == 2){
					$(this).html(_tmp[1]);
				}
			}); 
			// End -  Defect : 6200, 6232, 6231
		});
		// End -  Defect : 6200
	}
}

/**
 *  This will execute when AllNYCAgencies option is selected from drop down
 **/
function populateAllNYCAgencies(agencySet) {
	var agencies = agencySet.substring(1, agencySet.length - 1);
	var agencyArray = agencies.split(",");
	var provName = "";
	for ( var i = 0; i < agencyArray.length; i++) {
		var agencyString = agencyArray[i];
		provName = agencyString.substring(agencyString.indexOf("~")+1,agencyString.length);
		populatePreviousScreenValues(provName, "^AGENCY");
	}
}

/**
 *  This will execute when Finish button is clicked from Share document step 4
 **/
var finishURL = '';
$('#finish').click(function() { 

	pageGreyOut();
	// Start Updated in R5
	var folderId = $js("#leftTree").jstree("get_selected");
	$('#parentFolderId').val(folderId);
	document.share4.action = $("#finalSharing").val();
	
	var options = {
		success : function(responseText, statusText, xhr) {
			var response = new String(responseText);
			var responses = response.split("|");
			if(!(responses[1] == "Error" || responses[1] == "Exception"))
			{
				$(".overlay").closeOverlay();
				removePageGreyOut();
		        var data = $(responseText);
		        if(data.find(".tabularWrapper").size()>0)
		   		{
		        	$("#myform .tabularWrapper").replaceWith(data.find(".tabularWrapper"));
		 	        var _message = data.find("#message").val();
		 	        var _messageType = data.find("#messageType").val();
		 	       
		 	       
		 	        $(".messagediv").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
		 	       $(".messagediv").removeClass("failed passed");
		 	        $(".messagediv").addClass(_messageType);
		 			$(".messagediv").show();
		 			 $(".overlay").closeOverlay();
		 	        removePageGreyOut();
		   		}    
		// End Updated in R5
			}else
	        {
				$(".errorDiv")
				.html(responses[3]
								+ "<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('errorDiv', this)\" />");
				$(".errorDiv").addClass(responses[4]);
				$(".errorDiv").show();
				removePageGreyOut();
	
	        }
		},
		error : function(xhr, ajaxOptions, thrownError) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	};
	$(this.form).ajaxSubmit(options);
	return false;
});

/**
 *  This will execute when Cancel button is clicked from Share document step 4
 **/
$('#cancelshare4').click(function() {
	$(".overlay").closeOverlay();
	return false;
});

/**
 * This will execute when Finish button is clicked from Share document step 4 
 **/
function submitSuccess(str1, str2) {
	document.getElementById("message").value = str1;
	document.getElementById("messageType").value = str2;
	document.getElementById("nextAction4").value = "showdocumentlist";
	document.share4.submit();
}

/**
 * Updated in R5: This will execute when Finish button is clicked from Share document step 4
 **/
function shareScreen4(originalFormAction) {
document.share4.action = originalFormAction+"&submit_action=finalShareDocument";
}

/**
 * Updated in R5 :This will execute when Back button is clicked from Share document step 4
 **/
function backtoStep3(form) {
	document.share4.action = $('#backtoStep3SharingForm').val();
}
/**
 *  This function is called to enable finish button
 **/
function allowFinishButtonEnable(provAgenyList)
{
	if (provAgenyList.length != 0) 
	{
		$('#checkForFinish').prop("checked",!$('#checkForFinish').prop("checked"));
		finishEnable();
    }
}

/**
 * This will execute on load of Share document step 4 screen to populate selected provider and agencies value
 **/
function populateProviderAgencyList(providerAgencyList) {
	var table = document.getElementById("mytable3");
	var rowCount = table.rows.length;
	if (rowCount > 1) {
		for ( var i = 0; i < rowCount; i++) {
			table.deleteRow(-1);
		}
	}

	if (providerAgencyList.length == 0) {
		$("#errorDiv").html("No Provider/NYC Agency is selected");
		$("#errorDiv").addClass("failed").show();
		document.getElementById("finish").disabled = true;
		// Start Added in R5
		document.getElementById("checkForFinish").disabled=true;
		// End Added in R5
	}
	if ('null' != providerAgencyList) {
		var provAgencyArray = providerAgencyList.split("|");
		for ( var i = 0; i < provAgencyArray.length-1; i++) {
			var table = document.getElementById("mytable3");
			var rowCount = table.rows.length;
			var row = table.insertRow(rowCount);
			var cell1 = row.insertCell(0);
			var DisplayAgencyName = provAgencyArray[i].substring(0,
					provAgencyArray[i].indexOf("^"));
			$(cell1).append("<div>"+DisplayAgencyName+"</div>");
			var counter = 0;
			$("#mytable3 tr").each(function() {
				if (counter % 2 == 0) {
					$(this).css("background-color", "#f1f1f1");
				} else {
					$(this).css("background-color", "#ffffff");
				}
				counter++;
			});
		}
	}
}

/**
 * This will execute to delte table row on click of Remove link from Share screen 2 and 3
 **/
function deleteRow(val, tableId) {
	var rowId = document.getElementById(val);
	while (rowId.hasChildNodes()) {
		rowId.removeChild(rowId.lastChild);
	}
	rowId.parentNode.removeChild(rowId);
	applyCssToTable(tableId);
}

/**
 * This will execute to apply css to table generated dynamically
 **/
function applyCssToTable(tableId) {
	var counter = 0;
	$(tableId).each(function() {
		if (counter % 2 == 0) {
			$(this).css("background-color", "#f1f1f1");
		} else {
			$(this).css("background-color", "#ffffff");
		}
		counter++;
	});
}

/**
 * This will execute when selected provider or agency is added to table
 **/
function checkNameAlreadyExist(provName, tableId) {
	var table = document.getElementById(tableId);
	var rowCount = table.rows.length;
	var result = true;
	for ( var i = 0; i < rowCount; i++) {
		var rowobj = table.rows[i];
		var cell = rowobj.cells;
		if (provName == cell[0].innerHTML) {
			result = false;
			break;
		} else {
			result = true;
		}
	}
	return result;
}

/**
 * This will execute to close overlay at any time by clicking close button
 **/
$(".alert-box-removeselectedprovs").find('#closeoverlay').click(function() {
	$(".overlay").closeOverlay();
	return false;
});

/**
 *  This will execute when Remove Selected button is clicked from Shared link
 **/
function removeselectedprov(docId,docName,docType){
	pageGreyOut();
	// Start Updated in R5
	var folderId = $js("#leftTree").jstree("get_selected");
	$('#parentFolderIdUnshare').val(folderId);
	document.displaysharedform.action = document.displaysharedform.action+'&docTypeHidden='+docType+'&removeselected=true&documentId='+docId+'&documentName='+docName+"&submit_action=finalUnShareDocument";
	// End Updated in R5
}

/**
 * Updated in R5: This will execute when RemoveAll button is clicked from Shared link
 **/
function removeallprov(docId,documentName,docType){

	pageGreyOut();
	var folderId = $js("#leftTree").jstree("get_selected");
	$('#parentFolderIdUnshare').val(folderId);
	document.displaysharedform.action = document.displaysharedform.action+'&unshareBy=remove_all&documentId='+docId+'&documentName='+documentName+'&docType='+docType+"&submit_action=finalUnShareDocument";
}

/**
 * This will execute when any provider or agency is selected from Shared link
 **/
function enableButton(){
		var chks = document.getElementsByName('provCheck');
        var hasChecked = false;
        for (var i = 0; i < chks.length; i++)
          {
             if (chks[i].checked){hasChecked = true;}
          }
         if(hasChecked){
       	  	document.getElementById("removeselected").disabled = false;
         }else{
         	document.getElementById("removeselected").disabled = true;
         }
}

/**
 *  This will execute when Remove All or Remove Selected button is clicked from Shared link.
 **/
function submitSuccessForRemovedDocs(str1, str2){
	document.getElementById("message").value= str1;
	document.getElementById("messageType").value = str2;
	document.getElementById("nextDisplaySharedAction").value="showdocumentlist";
	document.displaysharedform.action = document.displaysharedform.action;
	document.displaysharedform.submit();
}

//Added in R5
/**
 * This method will enable finish button when checbox is:checked.
 */
function finishEnable(){
	if($("#checkForFinish").attr('checked')){
	$("#finish").attr('disabled',false);
	}else{
		$("#finish").attr('disabled',true);
	}
}
//Added in R5
/**
 * This method will unshare all the documents.
 */
function unshareAllDocument(formaction) {
	document.provform.action = provFormAction
			+ "&submit_action=unsharedocumentall";
}

