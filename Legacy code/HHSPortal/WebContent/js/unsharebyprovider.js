var docName, unshareByProviderAction;

//on load function to perform various checks on loading of jsp
function onReady() {
	$("#removeaccessbyprovider").attr("disabled", "disabled");
	unshareByProviderAction = document.unshareformbyprovider.action;
	// This will execute when Remove Access Button is clicked
	$(".alert-box-unsharebyprovider").find('#removeaccessbyprovider').click(
			function() { 
				removeaccessbyprovider(this.form);
				var options = {
					success : function(responseText, statusText, xhr) {
						response = new String(responseText);
						var responses = response.split("|");
						if (responses[1] == "Success") {
							$(".overlay").closeOverlay();
							//removePageGreyOut();
							submitSuccess(responses[3], responses[4]);
						}
					},
					error : function(xhr, ajaxOptions, thrownError) {
						showErrorMessagePopup();
						removePageGreyOut();
					}
				};
				$(this.form).ajaxSubmit(options);
				pageGreyOut();
				return false;
			});
	// this will execute when any option is selected from provider Name drop down
	$(".alert-box-unsharebyprovider").find('#providerName').change(function() {
		pageGreyOut();
		getDocumentForProvider();
	});
	// This will execute when Cancel button is clicked
	$(".alert-box-unsharebyprovider").find('#cancelunsharebyprovider').click(
			function() {
				$(".overlay").closeOverlay();
				return false;
			});
}

// This will execute when Remove Access button is clicked
function submitSuccess(str1, str2) {
	document.getElementById("message").value = str1;
	document.getElementById("messageType").value = str2;
	document.getElementById("nextUnshareByProviderAction").value = "showdocumentlist";
	unshareByProviderAction = unshareByProviderAction + "&removeNavigator=true";
	document.unshareformbyprovider.submit();
}

// This will execute when any option is selected from provider name drop down
// IT will get the document details based on the selected provider name
function getDocumentForProvider() {
	var table = document.getElementById("removetable");
	var rowCount = table.rows.length;
	if (rowCount > 1) {
		for ( var i = 1; i < rowCount; i++) {
			table.deleteRow(-1);
		}
	}
	var e = document.getElementById("providerName");
	var selectedInput = e.options[e.selectedIndex].value;
	if ($.trim(selectedInput) != '') {
		var url = $("#contextPathSession").val()+"/GetContent.jsp?providerName=" + escape(selectedInput);
		postRequest(url);
	}else{
		removePageGreyOut();
		$("#tableDiv").hide();
		$("#removeaccessbyprovider").attr("disabled", "disabled");
	}
}

// IT will process the above servlet request and get the response
function postRequest(strURL) {
	var xmlHttp;
	if (window.XMLHttpRequest) { 
		var xmlHttp = new XMLHttpRequest();
	} else if (window.ActiveXObject) { 
		var xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
	xmlHttp.open('POST', strURL, true);
	xmlHttp.setRequestHeader('Content-Type',
			'application/x-www-form-urlencoded');
	xmlHttp.onreadystatechange = function() {
		if (xmlHttp.readyState == 4) {
			displaytable(xmlHttp.responseText);
		}
	};
	xmlHttp.send(strURL);
}

// This will execute when Remove Access button is clicked
function removeaccessbyprovider(form) {
	document.getElementById("nextUnshareByProviderAction").value = "removeaccessbyprovider";
	form.action = unshareByProviderAction + "&removeNavigator=true&docIds="
			+ docName;
}

// This will execute to populate document details in table fecthed through servlet call based on provider or agency name selected
function displaytable(docdetail) {
	docName = "";
	if ('null' != docdetail) {
		var docArray = docdetail.split("|");
		if ('' != docArray) {
			for ( var i = 0; i < docArray.length - 1; i++) {
				var docProps = docArray[i].split("!");
				var table = document.getElementById("removetable");
				var rowCount = table.rows.length;
				var row = table.insertRow(rowCount);
				var cell1 = row.insertCell(0);
				var cell2 = row.insertCell(1);
				cell1.appendChild(document.createTextNode(docProps[0]));
				cell2.appendChild(document.createTextNode(docProps[1]));
				docName = docName + docProps[0];
				if (i < docArray.length - 1) {
					docName = docName + ",";
				}
				var counter = 0;
				$("#removetable tr").each(function() {
					if (counter % 2 == 0) {
						$(this).css("background-color", "#f1f1f1");
					} else {
						$(this).css("background-color", "#ffffff");
					}
					counter++;
				});
			}
			docName = docName.substring(0, docName.length - 1);
			$("#tableDiv").show();
			$("#removeaccessbyprovider").removeAttr("disabled");
		} else {
			$("#tableDiv").hide();
		}
	}
	removePageGreyOut();
}