/** 
 * This will execute when any servlet call is made   ddddddddddd
 **/
var _itemId=null;
var parallelProcessVar = null;

//var role_cur = (String)session.getAttribute("role_current");


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
			updatepage(xmlHttp.responseText);
		}
	};
	xmlHttp.send(strURL);
}

/**
 * R5 release code for menu container
 **/
function checkOpacity(){
	var atLeastOneIsChecked = $("input.isChecked:checked").length > 0;
	if (atLeastOneIsChecked) {
		$("#file_menu").css({
			opacity : 1.0
		});
	} else {
		$("#file_menu").css({
			opacity : 0.5
		});
	}
}
$(function() {
	
	$('#myOrgform').on('keypress ', function(e) {
		  var keyCode = e.keyCode || e.which;
		
		  if (keyCode === 13) {
			  if($('#SubmitButton').is(':disabled')){
				  e.preventDefault();
				  return false;
			  }
			  }
		  
		});
	//error message hide
	$("#searchform input[type=text]").on("keyup", function(){
	$('#messagediv').hide();
	$('#sharedWitherror').text("");
		//$(this).length
	} ); 
	$("#createFolderbutton").on("click", function() {
		$('.error').show();
		
	});
	//combo box for shared search
	$("#doctype_city").typeHeadDropDown({button:$("#combotable_button2"), optionBox: $("#dropdownul2")});
	//end

	
	$("#file_menu").fileMenuOptions();
	checkOpacity();
	
	$("a.upload-exit").on("click",function(){
		checkOpacity();
	});
	$("#searchform span.formfield,#deleteform span.formfield,#findorgdocform span.formfield,#findshareddocform span.formfield").keyup(function() {
	    if (!this.value) {
   	      $(this).parent().find("span.error").hide();
	    }
	});
	$('#newfolderform span.formfield #folderName').keyup(function(){
		 if (!this.value) {
			 $("#errorId").hide();
		 }
	});
	$("#filter1,#filter,#filter2,#filter4,#filter3").on("click",function(){
		$("div.row").find("span.error").show();
	});
	$(document).on('change', 'input.isChecked', function() {
		var atLeastOneIsChecked = $("input.isChecked:checked").length > 0;
		if (atLeastOneIsChecked) {
			$("#file_menu").css({
				opacity : 1.0
			});
		} else {
			$("#file_menu").css({
				opacity : 0.5
			});

		}
	});
	$("#file_menu,#chatbox-content1").on({
		mouseenter : function() {
			var atLeastOneIsChecked = $("input.isChecked:checked").length > 0;
			if (atLeastOneIsChecked) {
				$("#file_menu").addClass("file_menu_hover");
			}
		},
		mouseleave : function() {

			$("#file_menu").removeClass("file_menu_hover");
			
		}
	});
	
	$('#searchDocument').hover(function(){
		$(this).addClass("file_menu_hover");
	},function(){
		$(this).removeClass("file_menu_hover");
	});
	
	$("#file_menu").on("click",function(event){
		var selectedValue= $js("#leftTree").jstree("get_selected");
		var index = selectedValue[0].indexOf("RecycleBin");
		if(index !== -1)
		{ 
			$("#chatbox-content1").hide();
			$("#chatbox-content2").hide();
			$("#chatbox-content3").hide();
			$("#chatbox-content-recyclebin-header").show();
		}
		else{
			var showShareButton = true;
			var showUnShareButton = true;
			$('#documentVaultGrid input[name="check"]:checked').each(function() {
				var res = (this.value).split(",");
				var searchFlag = $('#clickedSearch').val();
				if(searchFlag == 'clickedSearch' && res[3] != '0')
				{
				$("#chatbox-content-recyclebin-header, #chatbox-content2, #chatbox-content3").hide();
				$("#chatbox-content1").show();
				return false;
				}
				
				if(res[3] == '0')
				{		
						if(showShareButton)
						{
							showShareButton = true;
						}
						showUnShareButton = false;
				}
				else if(res[0] != res[2])
				{
					showShareButton = false;
					showUnShareButton = false;
				}
				else
				{
					if(showShareButton && showUnShareButton)
					{
					showShareButton = true;
					showUnShareButton = true;
					}
				}
			});
			if(showShareButton && showUnShareButton){
				$("#chatbox-content-recyclebin-header, #chatbox-content2, #chatbox-content3").hide();
				$("#chatbox-content1").show();
				return false;
				}
			else if(!showShareButton && !showUnShareButton){
				$("#chatbox-content-recyclebin-header, #chatbox-content2, #chatbox-content1").hide();
				$("#chatbox-content3").show();
				return false;
			}
			else if(showShareButton && !showUnShareButton){
				$("#chatbox-content1, #chatbox-content-recyclebin-header, #chatbox-content3").hide();
				$("#chatbox-content2").show();
				return false;
			}
			
		}
	});
});

$.fn.fileMenuOptions = function(){
	$("#file_menu").on("click",function(e){
		if($("input.isChecked:checked").length > 0){ e.stopPropagation();
		$(this).find(".fileoptions1").toggle();
		}
	});
};


/** 
 * Displays file options chat box for vault
 **/
function displayFileOptions(elt, event, shareFlag) {
	//<!-- CQ 8914 read only role R7.2.0 -->
	var role_observer = document.getElementById("role_current");
	//console.log('---displayFileOptions---');
	//console.log('---role_current: '+role_observer.value);
	
  	if((null != shareFlag && shareFlag == 'false' ) || ($('#readOnlyCheck').val() == 'R') || (role_observer.value == 'OBSERVER'))
	{
		var $parent = $(elt).closest("td");
		var $contentBox = $parent.find(".chatbox-content-shared");
	    if($parent.find(".fileoptions1").size()==0){
	    	$('div.fileoptions1').hide();
	    	var $div = $("<div>", {id: "foo", "class": "fileoptions1 fileoptions1-hoveralignment"});
	    	$div.css('left',event.pageX-35);      
		    $div.css('top',event.pageY+15);
		    $div.css({"position" : "absolute"});
	    	$(elt).after($div);
	    	$($div).append($contentBox);
	    	$parent.find(".chatbox-content-shared").show();
	    	$('div.fileoptions1').on('mouseleave',function(e){
				$(this).hide();
			});
			$parent.find('div.fileoptions1').show();
		} else {
			$parent.find('div.fileoptions1').toggle(200);
			$parent.find('div.fileoptions1').css('left',event.pageX-35);
			$parent.find('div.fileoptions1').css('top',event.pageY+15);
		}
	}
	else
	{
		var $parent = $(elt).closest("td");
		var $contentBox = $parent.find("#chatbox-content");
		if($parent.find(".fileoptions1").size()==0){
			$('div.fileoptions1').hide();
			var $div = $("<div>", {id: "foo", "class": "fileoptions1 fileoptions1-hoveralignment"});
			$div.css('left',event.pageX-35);      
			$div.css('top',event.pageY+15);
			$div.css({"position" : "absolute"});
			$(elt).after($div);
			$($div).append($contentBox);
			$parent.find("#chatbox-content").show();
			$('div.filemenuoptions').on('mouseleave',function(e){
				$('div.fileoptions1').hide();
			});
			$parent.find('div.fileoptions1').show();
			$(".chatbox-content-shared").hide();
		} else {
			$parent.find('div.fileoptions1').toggle(200);
			$parent.find('div.fileoptions1').css('left',event.pageX-35);
			$parent.find('div.fileoptions1').css('top',event.pageY+15);
		}
	}
}
/**
 * Displays file options chat box for Recycle Bin
 **/
function displayFileOptionsRecycleBin(elt, event,shareFlag) {
   //<!-- CQ 8914 read only role R7.2.0 -->
    var role_observer = document.getElementById("role_current");
    //console.log('---displayFileOptionsRecycleBin---');
	//console.log('---role_current: '+role_observer.value);
	
	if((null != shareFlag && shareFlag == 'false' ) || ($('#readOnlyCheckForRecycleBin').val() == 'R') || (role_observer.value == 'OBSERVER'))
	{
		var $parent = $(elt).closest("td");
		var $contentBox = $parent.find(".chatbox-content-shared");
	    if($parent.find(".fileoptions1").size()==0){
	    	$('div.fileoptions1').hide();
	    	var $div = $("<div>", {id: "foo", "class": "fileoptions1"});
	    	$div.css('left',event.pageX-35);      
		    $div.css('top',event.pageY+15);
		    $div.css({"position" : "absolute"});
	    	$(elt).after($div);
	    	$($div).append($contentBox);
	    	$parent.find(".chatbox-content-shared").show();
	    	$('div.fileoptions1').on('mouseleave',function(e){
				$(this).hide();
			});
			$parent.find('div.fileoptions1').show();
		} else {
			$parent.find('div.fileoptions1').toggle(200);
			$parent.find('div.fileoptions1').css('left',event.pageX-35);
			$parent.find('div.fileoptions1').css('top',event.pageY+15);
		}
	}
	else
	{
		var $parent = $(elt).closest("td");
		var $contentBox = $parent.find("#chatbox-content-recyclebin");
	    if($parent.find(".fileoptions1").size()==0){
	    	$('div.fileoptions1').hide();
	    	var $div = $("<div>", {id: "foo", "class": "fileoptions1"});
	    	$div.css('left',event.pageX-35);      
		    $div.css('top',event.pageY+15);
		    $div.css({"position" : "absolute"});
	    	$(elt).after($div);
	    	$($div).append($contentBox);
	    	$parent.find("#chatbox-content-recyclebin").show();
	    	$('div.filemenuoptions').on('mouseleave',function(e){
				$('div.fileoptions1').hide();
			});
	    	$parent.find('#chatbox-content-recyclebin').css({"width" : "110px"});
	    	$parent.find('#chatbox-content-recyclebin').parent().css({"width" : "110px"});
			$parent.find('div.fileoptions1').show();
		} else {
			$parent.find('div.fileoptions1').toggle(200);
			$parent.find('div.fileoptions1').css('left',event.pageX-35);
			$parent.find('div.fileoptions1').css('top',event.pageY+15);
		}
	}
}
// End of the code
/** 
 * This will execute to get filter document type for filter document category
 **/
function updatepage(str) {
	var n = str.split("|");
	var selectbox = document.getElementById("filtertype");
	var i;
	for (i = selectbox.options.length - 1; i >= 0; i--) {
		selectbox.remove(i);
	}
	if (null != n) {
		var optn = document.createElement("OPTION");
		optn.text = "";
		optn.value = "";
		optn.setAttribute("title", "");
		selectbox.options.add(optn);
		for ( var i = 0; i < n.length - 1; i++) {
			var optn = document.createElement("OPTION");
			optn.text = n[i];
			optn.value = n[i];
			optn.setAttribute("title", n[i]);
			selectbox.options.add(optn);
		}
	}
}

/**
 * This will execute when Filter Documents tab is clicked or closed Updated
 * Method in R4
 */
// changes done for defect 6253
function setVisibility(id, visibility) {

	callBackInWindow("closePopUp");
	if ($("#" + id).is(":visible") && $("#filterStatus").val() != 'filtered') {
		clearFilter('null', $("#providerSet").val(), $("#agencySet").val());
	}
	$("#" + id).toggle();
}

/**
 * This will be executed when any option is selected from the action drop down
 * Updated Method in R4
 */
function openDocument(documentId, selectElement, documentName) {
	var value = selectElement.selectedIndex;
	if (value == 1) {
		viewDocument(documentId, documentName);
		selectElement.selectedIndex = "";
	} else if (value == 2) {
		pageGreyOut();
		$("#next_action").val('viewDocumentInfo');
		document.provform.action = provFormAction + '&documentId=' + documentId
				+ '&removeNavigator=true&action=documentVault';
		document.provform.submit();
	} else if (value == 3) {
		pageGreyOut();
		selectElement.selectedIndex = "";
		$("#next_action").val('checkLinkDocumentBeforedelete');
		document.provform.action = provFormAction + '&documentId=' + documentId
				+ '&removeNavigator=true';
		// On click of 'delete document' option from action dropdown an ajax
		// call is made rather than form submit to avoid refreshing the page.
		var options = {
			success : function(responseText, statusText, xhr) {
				var responseString = new String(responseText);
				var responsesArr = responseString.split("|");
				if (responsesArr[4] == "confirmation") {
					removePageGreyOut();
					deleteDocumentId = documentId;
					$(".overlay").launchOverlay($(".alert-box-delete"),
							$(".nodelete"), "350px", null, null);
					// Added for 1795 : This will execute when we click on the X
					// button on the overlay
					$(".nodelete")
							.unbind("click")
							.click(
									function() {
										document.provform.action = provFormAction
												+ '&documentId='
												+ deleteDocumentId
												+ '&removeNavigator=true&submit_action="canceldelete"';
										var options = {
											success : function(responseText,
													statusText, xhr) {
												$(".overlay").closeOverlay();
											},
											error : function(xhr, ajaxOptions,
													thrownError) {
												showErrorMessagePopup();
												removePageGreyOut();
											}
										};
										$(document.provform)
												.ajaxSubmit(options);
									});
				} else if (responsesArr[4] == "Draft")// -- added for Release
														// 3.5.0, QC 5630 -->
				{
					removePageGreyOut();
					deleteDocumentId = documentId;
					$(".overlay").launchOverlay($(".alert-box-warning"),
							$(".nodelete"), "350px", null, "onReady");
					$(".nodelete")
							.unbind("click")
							.click(
									function() {
										document.provform.action = provFormAction
												+ '&documentId='
												+ deleteDocumentId
												+ '&removeNavigator=true&submit_action="canceldelete"';
										var options = {
											success : function(responseText,
													statusText, xhr) {
												$(".overlay").closeOverlay();
											},
											error : function(xhr, ajaxOptions,
													thrownError) {
												showErrorMessagePopup();
												removePageGreyOut();
											}
										};
										$(document.provform)
												.ajaxSubmit(options);
									});
				} else {
					removePageGreyOut();
					$(".messagediv")
							.html(
									responsesArr[3]
											+ "<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
					$(".messagediv").addClass(responsesArr[4]);
					$(".messagediv").show();
				}
			},
			error : function(xhr, ajaxOptions, thrownError) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		};
		$(document.provform).ajaxSubmit(options);
		return false;
	}
}
function myfunc() {
}
/** 
 * This will execute when upload button is clicked
 **/
function uploadDocument(formaction) {
	pageGreyOut();
	document.getElementById("next_action").value = "documentupload";
	document.provform.action = formaction;
}
// Added for Release 5

/** 
 * This will execute when Search Button tab is clicked
 **/
function displayFilter(formaction) {
	var isValid = true;
	$("input[type='text']").each(function() {
		if ($(this).attr("validate") == 'calender') {
			if (verifyDate(this)) {
				var fromDate = $("#modifiedfrom").val();
				var toDate = $("#modifiedto").val();
				if (Date.parse(toDate) < Date.parse(fromDate)) {
					$("#dateRange").html('! This range is not valid');
					isValid = false;
					return false;
				}
			} else {
				isValid = false;
			}
		}
	});

	if (isValid) {
		pageGreyOut();

		document.provform.action = formaction + "&isFilter=" + true + "&folderId=" + ""
				+ "&submit_action=filterdocuments";

		$("#searchDoc").slideUp();
		var options = {
			success : function(responseText, statusText, xhr)

			{
				var response = new String(responseText);

				var responses = response.split("|");
				if (!(responses[1] == "Error" || responses[1] == "Exception")) {
					removePageGreyOut();
					$(".messagediv").hide();
					var data = $(responseText);
					if (data.find(".tabularWrapper").size() > 0) {
						$(".tabularWrapper").replaceWith(
								data.find(".tabularWrapper"));
						$(".overlay").closeOverlay();
						removePageGreyOut();
					}
				} else {
					$(".messagediv")
					.html(
							responsesArr[3]
									+ "<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
			$(".messagediv").addClass(
					responsesArr[4]);
			$(".messagediv").show();
			$(".overlay")
					.closeOverlay();
			removePageGreyOut();
		}
			}

		};
		$(document.provform).ajaxSubmit(options);
		return false;
	}
}

/** 
 * This will execute when Clear Filter button is clicked
 **/
function clearFilter(sharedFlag, providerSet, agencySet) {
	document.getElementById("profiltercategory").value = "";
	document.getElementById("filtertype").disabled = true;
	document.getElementById("filtertype").value = "";
	document.getElementById("provider").value = "";
	document.getElementById("agency").value = "";
	document.getElementById("modifiedfrom").value = "";
	document.getElementById("modifiedto").value = "";
	providerSet = providerSet.replace('[]', '');
	agencySet = agencySet.replace('[]', '');
	if (((providerSet != 'null') || (agencySet != 'null'))
			&& (($.trim(providerSet) != "") || ($.trim(agencySet) != ""))) {
		$("#sharedDiv").show();
		$('#providerDiv').show();
		$('#agencyDiv').show();
		updateProviderAndAgency(providerSet, agencySet);
		if (sharedFlag != "shared" && sharedFlag != "unshared") {
			$("#rdoBoth").attr('checked', true);
		}
		if (sharedFlag == "unshared") {
			$('#providerDiv').hide();
			$('#agencyDiv').hide();

		}
	}
	$("input[type='text']").each(function() {
		if ($(this).attr("validate") == 'calender') {
			$(this).parent().next().html("");
		}
	});
}

/** 
 * This will execute when any option is selected for filter Document Category
 **/
function filterCategoryForProvider() {
	var e = document.getElementById('profiltercategory');
	var category = e.options[e.selectedIndex].value;
	if (category == null || category == "") {
		document.getElementById("filtertype").value = "";
		document.getElementById("filtertype").disabled = true;
		return false;
	} else {
		document.getElementById("filtertype").disabled = false;
		return true;
	}

}

/** 
 * This will execute when Previous,Next.. is clicked for pagination
 **/
function paging(pageNumber) {
	var folderId;
	var parentId = 'child';
	var folderIdArray = $js("#leftTree").jstree("get_selected");
	var sharedPageOrg = $("#sharedPageOrg").val();


	if(folderIdArray.length <= 0 && $js('#leftTree').jstree(true))
	{
		folderIdArray = $js("#selectOrgnization").jstree("get_selected");
		folderId = folderIdArray[0];
		if(folderIdArray.length > 0 && $js('#selectOrgnization').jstree(true))
		{
			treeOrgType = $js('#selectOrgnization').jstree(true).get_node($js("#selectOrgnization").jstree("get_selected")).data.organizationType;
		}
	}
	else
	{
		folderId = folderIdArray[0];
		treeOrgType = $js('#leftTree').jstree(true).get_node($js("#leftTree").jstree("get_selected")).data.organizationType;
		if($js('#leftTree').jstree(true).get_node($js("#leftTree").jstree("get_selected")).parent == '#')
		{
			parentId = 'root';
		}
	}
	var jspName = 'provdocumentlist';
	if(null != folderId && typeof folderId == 'string' && folderId.indexOf('Recycle') > -1)
	{
	jspName = 'recyclebin';
	}

	// Adding pageGreyOut for Release 5
	pageGreyOut();
	var previousPage = document.getElementById("pageIndex").value;
	document.getElementById("pageIndex").value = pageNumber;
	if ($("#searchDocumentVaultId").val() == 'documentVault') {
		document.provform.action = provFormAction + "&nextPage=" + pageNumber
				+ "&action=documentVault";
	} else {
		document.provform.action = provFormAction + "&parentId="+parentId+"&jspName="+jspName+"&folderId="+folderId+"&action=documentVault&nextPage=" + pageNumber;
	}
	// Adding below code for Release 5
	var options = {
		success : function(responseText, statusText, xhr) {
			removePageGreyOut();
			var data = $(responseText);
			$("#documentVaultGrid.tabularWrapper").html(data.find(".tabularWrapperDocumentVault").html());
			$('body').scrollTop(0);
			$('#documentVaultGrid').scrollTop(0);
			removePageGreyOut();
		}

	};
	if($('#sharedSearch').length && $('#sharedSearch').val() != '')
		{
		var jsp;
		var sortBy = $('#sortBy').val();	
		var sortType = $('#sortType').val();	
		if($('#sharedSearch').val() == 'true')
		{
			if($('#sharedSearchJspFlag').val() == 'true')
			{
			jsp = 'provdocumentlist';
			
			}
		else
			{
			jsp = 'sharedSearchAgency';
			}
		document.findshareddocform.action = findshareddocform
		+"&isFilter=true&sharedFlag="+$('#sharedSearch').val()+"&parentId="+parentId+"&action=documentVault&jspName="+jsp+"&nextPage=" + pageNumber+"&sortBy="+sortBy+"&sortType="+sortType;
		$(document.findshareddocform).ajaxSubmit(options);
		}
		else if($('#sharedSearch').val() == 'false')
		{
			document.findorgdocform.action = findOrgDocForm
			+"&isFilter=true&sharedFlag="+$('#sharedSearch').val()+"&parentId="+parentId+"&action=documentVault&jspName=sharedSearchAgency&nextPage=" + pageNumber+"&sortBy="+sortBy+"&sortType="+sortType;
			$(document.findorgdocform).ajaxSubmit(options);
		}
		}
	else
		{
		document.provform.action = provFormAction + "&parentId="+parentId+"&jspName="+jspName+"&folderId="+folderId+"&action=documentVault&nextPage=" + pageNumber
		+ "&cityUserSearchProviderId=" + sharedPageOrg;
		$(document.provform).ajaxSubmit(options);
		}

	return false;
}

/**
 * This will execute when any Column header on grid is clicked for sorting
 * Updated Method in R4
 */
function sort(columnName) {
	// Adding pagegreyout for Release 5
	pageGreyOut();
	var parentId = 'child';
	var folderId;
	var folderIdArray = $js("#leftTree").jstree("get_selected");
	var treeOrgType;
	
	if(folderIdArray.length <= 0 && $js('#leftTree').jstree(true))
	{
		folderIdArray = $js("#selectOrgnization").jstree("get_selected");
		folderId = folderIdArray[0];
		if(folderIdArray.length > 0 && $js('#selectOrgnization').jstree(true))
		{
			treeOrgType = $js('#selectOrgnization').jstree(true).get_node($js("#selectOrgnization").jstree("get_selected")).data.organizationType;
		}
	}
	else
	{
		folderId = folderIdArray[0];
		treeOrgType = $js('#leftTree').jstree(true).get_node($js("#leftTree").jstree("get_selected")).data.organizationType;
		if($js('#leftTree').jstree(true).get_node($js("#leftTree").jstree("get_selected")).parent == '#')
			{
			parentId = 'root';
			}
	}
	var jspName = 'provdocumentlist';
	if(null != folderId && typeof folderId == 'string' && folderId.indexOf('Recycle') > -1)
	{
		jspName = 'recyclebin';
	}
	var sortBy = document.getElementById("sortBy");	
	var sortType = document.getElementById("sortType");
	var sortTypeMap = {date : "desc",docName : "asc", docType : "asc", shareStatus : "desc",linkStatus : "desc",OrgName : "asc"};
	if (sortBy.value.toLowerCase() == columnName.toLowerCase()) {
		if(sortType.value.toLowerCase() == "asc")
			sortType.value = "desc";
		else
			sortType.value = "asc";
	} else {
		sortType.value = sortTypeMap[columnName];
	}
	sortBy.value = columnName;
	if ($("#searchDocumentVaultId").val() == 'documentVault') {
		document.provform.action = provFormAction + "&sortBy=" + columnName
				+ "&sortType=" + sortType.value + '&submit_action=documentVault';
	} else {
		document.provform.action = provFormAction + "&sortBy=" + columnName
				+ "&sortType=" + sortType.value + '&submit_action=documentVault';
	}
	// Adding below code for release 5
	var options = {
		success : function(responseText, statusText, xhr) {
			removePageGreyOut();
			var data = $(responseText);
			$(".tabularWrapper").html(data.find(".tabularWrapper").html());
			removePageGreyOut();
		}

	};
	if($('#sharedSearch').length && $('#sharedSearch').val() != '')
		{
		var jsp;
		if($('#sharedSearch').val() == 'true')
			{
			if($('#sharedSearchJspFlag').val() == 'true')
				{
				jsp = 'provdocumentlist';
				
				}
			else
				{
				jsp = 'sharedSearchAgency';
				}
			document.findshareddocform.action = findshareddocform
			+"&treeOrgType="+treeOrgType+"&submit_action=filterdocuments&parentId="+parentId+"&isFilter=true&sharedFlag="+$('#sharedSearch').val()+"&jspName="+jsp+"&action=documentVault"
			 + "&sortBy=" + columnName
				+ "&sortType=" + sortType.value;
			$(document.findshareddocform).ajaxSubmit(options);
			}
		else if($('#sharedSearch').val() == 'false')
			{
			document.findorgdocform.action = findOrgDocForm
			+"&treeOrgType="+treeOrgType+"&submit_action=filterdocuments&parentId="+parentId+"&isFilter=true&sharedFlag="+$('#sharedSearch').val()+"&jspName=sharedSearchAgency&action=documentVault"
			 + "&sortBy=" + columnName
				+ "&sortType=" + sortType.value;
			$(document.findorgdocform).ajaxSubmit(options);
			}
		
		}
	else
		{
		document.provform.action = provFormAction+"&treeOrgType="+treeOrgType+"&parentId="+parentId+"&jspName="+jspName+"&action=documentVault&submit_action=filterdocuments&folderId="+folderId + "&sortBy=" + columnName
		+ "&sortType=" + sortType.value;;
		$(document.provform).ajaxSubmit(options);
		}
	
	return false;
}

/** 
 * This will execute when Share button is clicked
 **/
function shareDocument(formaction,documentId) {
	document.provform.action = provFormAction
			+ "&submit_action=shareDocumentStep1&checkedDocumentId="+documentId;
}

 /**
  * This will execute when UnShareAll button is clicked
  **/
function unshareAllDocument(formaction) {
	document.provform.action = provFormAction
	+ "&submit_action=unsharedocumentall";
}

/** 
 * This will execute when UnShareByProvider button is clicked
 **/
function unshareDocumentByProvider(formaction) {
	document.getElementById("next_action").value = "unsharedocumentbyprovider";
}

/** 
 * This will execute when Shared link is clicked
 **/
function displaySharedDocuments(documentId, name, formaction) {
	document.provform.action = formaction + '&documentId=' + documentId
			+ '&documentName=' + name + '&submit_action=getSharingStatus';
}

 /**
  * This will execute on load of jsp to enable disable unshareByProvider button
  *  based on document shared status
  */
function checkShareStatus(shareStatus, providerSet, agencySet) {
	if (null != document.getElementById("unshareByProvider")) {
		if ('null' != shareStatus && shareStatus == "true") {
			document.getElementById("unshareByProvider").disabled = false;
			document.getElementById("unshareByProvider").className = "unShare";
		} else {
			if ((providerSet.replace('[]', '') == '' && agencySet.replace('[]',
					'') == '')
					|| (providerSet == 'null' && agencySet == 'null')) {
				document.getElementById("unshareByProvider").disabled = true;
				$("#sharedDiv").hide();
				$("#providerDiv").hide();
				$("#agencyDiv").hide();
			}
		}
	}
}

/**
 * This will execute on load of jsp to populate provider and agency drop downs
 * for filter based on document shared status
 */
function updateProviderAndAgency(providerSet, agencySet, selectedProv,
		selectedAgency) {
	if ('null' != providerSet) {
		var providers = providerSet.substring(0, providerSet.length);
		var provArray = providers.split("k3yv@lu3S3p@r@t0r");
		var selectboxprov = document.getElementById("provider");

		for ( var j = selectboxprov.options.length - 1; j >= 0; j--) {
			selectboxprov.remove(j);
		}
		var optn = document.createElement("OPTION");
		optn.text = "";
		optn.value = "";
		optn.setAttribute("title", "");
		selectboxprov.options.add(optn);
		if (provArray.length == 1 && provArray[0].length == 0) {
			document.getElementById("provider").disabled = true;
		}
		for ( var i = 0; i < provArray.length; i++) {
			var optn = document.createElement("OPTION");
			if (i > 0) {
				provArray[i] = provArray[i].substring(0, provArray[i].length);
			}
			optn.text = provArray[i];
			optn.value = provArray[i];
			optn.setAttribute("title", provArray[i]);
			if (provArray[i] == selectedProv) {
				optn.selected = "selected";
			}
			selectboxprov.options.add(optn);
		}
	}
	if ('null' != agencySet) {
		var agencies = agencySet.substring(1, agencySet.length - 1);
		var agencyArray = agencies.split(",");
		var selectboxagency = document.getElementById("agency");

		for ( var j = selectboxagency.options.length - 1; j >= 0; j--) {
			selectboxagency.remove(j);
		}
		var optn = document.createElement("OPTION");
		optn.text = "";
		optn.value = "";
		optn.setAttribute("title", "");
		selectboxagency.options.add(optn);
		if (agencyArray.length == 1 && agencyArray[0].length == 0) {
			document.getElementById("agency").disabled = true;
		}
		for ( var i = 0; i < agencyArray.length; i++) {
			var optn = document.createElement("OPTION");
			if (i > 0) {
				agencyArray[i] = agencyArray[i].substring(1,
						agencyArray[i].length);
			}
			optn.text = unescape(agencyArray[i]);
			optn.value = unescape(agencyArray[i]);
			optn.setAttribute("title", agencyArray[i]);
			if (agencyArray[i] == selectedAgency) {
				optn.selected = "selected";
			}
			selectboxagency.options.add(optn);
		}
	}
	if ('null' == agencySet && 'null' == providerSet) {
		document.getElementById("agency").disabled = true;
		document.getElementById("provider").disabled = true;
	}
}

/** 
 * This will execute when unshared radio button is clicked from filter
 **/
function removeProviderAndAgency() {
	var selectboxprov = document.getElementById("provider");
	for ( var j = selectboxprov.options.length - 1; j >= 0; j--) {
		selectboxprov.remove(j);
	}
	var selectboxagency = document.getElementById("agency");
	for ( var j = selectboxagency.options.length - 1; j >= 0; j--) {
		selectboxagency.remove(j);
	}
}

/**
 * This will execute to close any overlay opened on base jsp Updated Method in
 * R4
 */
function closeOverLay(provFormAction) {
	pageGreyOut();
	$("#next_action").val('viewDocumentInfo');
	document.uploadform.action = provFormAction
			+ '&removeNavigator=true&message=null&messageType=null';
	document.uploadform.submit();
}

/**
 *  This will execute to select all the displayed documents on single click and
 *  perform Share, UnShare functions
 */
function selectAllDisplayDocuments() {
	if (document.provform.selectAllDocuments.checked = true && document.provform.selectAllDocuments.value == "Check All") {
		for ( var a = 0; a < document.provform.check.length; a++) {
			document.provform.check[a].checked = true;
		}
		document.provform.selectAllDocuments.value = "UncheckAll";
	} else {
		for ( var a = 0; a < document.provform.check.length; a++) {
			document.provform.check[a].checked = false;
		}
		document.provform.selectAllDocuments.value = "Check All";
	}
}

/**
 *  This will execute when any servlet call is made for fetching doc type
 **/
function postRequestFetchDocType(strURL) {
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
			updatepageFetchDocType(xmlHttp.responseText);
		}
	};
	xmlHttp.send(strURL);
}
/** 
 * This Method Fetches the Document Type from the selected Document Category.
 **/
function updatepageFetchDocType(str) {
	var n = str.split("|");
	var selectbox = document.getElementById("filtertype");
	var i;
	for (i = selectbox.options.length - 1; i >= 0; i--) {
		selectbox.remove(i);
	}
	if (null != n) {
		var optn = document.createElement("OPTION");
		optn.text = "";
		optn.value = "";
		optn.setAttribute("title", "");
		selectbox.options.add(optn);
		for ( var i = 0; i < n.length - 1; i++) {
			var optn = document.createElement("OPTION");
			optn.text = n[i];
			optn.value = n[i];
			optn.setAttribute("title", n[i]);
			selectbox.options.add(optn);
		}
	}
}
/**
 * Added for R5
 * This function is added to delete file/folder from vault 
 */
function deleteData(documentId, documentType, sharingStatus) {
	var checkedItem = "";
	$("#documentVaultGrid input[type='checkbox']:checked").each(function(){
		if($(this).val() != "on")
		{
		checkedItem += $(this).val().split(",")[0]+','+$(this).val().split(",")[1] +"~";
		}
	});
	if(documentId == '')
		{
		$('#menuFlag').val("true");
		}
	else
		{
		$('#menuFlag').val("false");
		}
	$('#childToMove').val(documentId);
	$('#checkedDocumentId').val(documentId);
	$('#checkedDocumentType').val(documentType);
	var jqxhr = $.ajax({
	url : $("#contextPathSession").val()+"/GetContent.jsp?action=checkDocExits&documentId="+documentId+"&documentType="+documentType+"&jspName="+$('#presentFolderId').val(),
	type : 'POST',
	data: {checkedItem :  checkedItem },
	success : function(data) 
	{
		if(data == 'FilenotFound')
		{	
			removePageGreyOut();
			var _message= "The selected file/folder has been deleted.";
			openRootNodeInTree('leftTree');
			$(".messagediv").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
	  		$(".messagediv").addClass("failed");
	  		$(".messagediv").show();
		}
		else
			{
	var url = $("#contextPathVal").val() + '/GetContent.jsp?lockingFlag=true&nextAction=delete';
	$('#myform').attr('action', url);
	var optionsForMove = {
		success : function(responseText, statusText, xhr) {
			if (responseText == '' || responseText == 'true') {
				$(".messagediv")
						.html(
								"A transaction is currently in progress for the selected documents. Please try again at a later time."
										+ "<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
				$(".messagediv").addClass('failed');
				$(".messagediv").show();
				$(".overlay").closeOverlay();
				removePageGreyOut();
			} else {
				$('#redMessage').hide();
				var chks = document.getElementsByName('check');
				var hasShared = false;
				if (documentId != '') {
					if (sharingStatus === '1' || sharingStatus === '2') {
						hasShared = true;
					}
				} else {
					if (sharingStatus === '1' || sharingStatus === '2') {
						hasShared = true;
					} else {
						for ( var i = 0; i < chks.length; i++) {
							if (chks[i].checked) {
								var chksval = chks[i].value.split(",");
								if (chksval[2] === '1' || chksval[2] === '2') {
									hasShared = true;
								}
							}
						}
					}
				}
				if (hasShared || (($('input[type="checkbox"]:checked').parent().parent().find('img[title*="shared"]').size()>0)==true)) {
					$('#redMessage').show();
				}
				$(".overlay").launchOverlay($(".alert-box-delete"), $(".exit-panel"), "400px", null, null);
			}
		},
		error : function(xhr, ajaxOptions, thrownError) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	};
	$(document.provform).ajaxSubmit(optionsForMove);
			}
	},
		error : function(data, textStatus, errorThrown) {
		},
		complete : function() {
		}
	});
}

/** 
 * This function is called when moving a file or folder to different location.
 **/
function move(documentId, docType) {
	var checkedItem = "";
	$("#documentVaultGrid input[type='checkbox']:checked").each(function(){
		if($(this).val() != "on")
		{
		checkedItem += $(this).val().split(",")[0]+','+$(this).val().split(",")[1] +"~";
		}
	});
	if(documentId == '')
	{
		$('#menuFlag').val("true");
	}
else
	{
	$('#menuFlag').val("false");
	}
	$('#checkedDocumentType').val(docType);
	$('#checkedDocumentId').val(documentId);
	$('#childToMove').val(documentId);
	var selectedFolderId ;
	if($js("#leftTree").jstree(true))
	{
		selectedFolderId = $js("#leftTree").jstree().get_parent($js("#leftTree").jstree("get_selected"));
		
	}
	else
	{
		selectedFolderId = $js("#selectOrgnization").jstree().get_parent($js("#selectOrgnization").jstree("get_selected"));
	}
	var jqxhr = $.ajax(
	{
		url : $("#contextPathSession").val()+"/GetContent.jsp?action=checkDocExits&documentId="+documentId+"&documentType="+docType+"&jspName="+$('#presentFolderId').val(),
		type : 'POST',
		data: {checkedItem :  checkedItem },
		success : function(data) 
		{
			if(data == 'FilenotFound')
			{	
				removePageGreyOut();
				var _message= "The selected file/folder has been deleted.";
				openRootNodeInTree('leftTree');
				$(".messagediv").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
		  		$(".messagediv").addClass("failed");
		  		$(".messagediv").show();
			}
			else
			{
				url = $("#contextPathVal").val()+'/GetContent.jsp?lockingFlag=true&nextAction=move';
				$('#myform').attr('action', url);
				var optionsForMove = {
						success : function(responseText,statusText, xhr) {
							if(responseText == '' || responseText == 'true')
							{
								$(".messagediv")
										.html(
												"A transaction is currently in progress for the selected documents. Please try again at a later time."
														+ "<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
								$(".messagediv").addClass('failed');
								$(".messagediv").show();
								$(".overlay").closeOverlay();
								removePageGreyOut();
							}
							else
							{
								$js('#moveTree').jstree("destroy");
								$('.messagedivover').empty();
								$('.messagedivover').hide();
								$('.error').empty();
								$('#selectedfolderidformove').val("");
								$(".overlay").launchOverlay($(".alert-box-move"), $(".exit-panel"), "350px", "auto", null);
								tree($('#hdnopenTreeAjaxVar').val(), 'moveTree', 'selectedfolderidformove','','');
							}
						},
						error : function(xhr, ajaxOptions,
								thrownError) {
							showErrorMessagePopup();
							removePageGreyOut();
						}
				};
				$('#checkedDocumentType').val(docType);
				$(document.provform).ajaxSubmit(optionsForMove);
			}
		},
		error : function(data, textStatus, errorThrown) {
		},
		complete : function() {
		}
	});
}
/**
 * This function is called when a file or folder is restored from recycle bin.
 **/
function restore(documentId, docType)
{ 
	var checkedItem = "";
	$("#documentVaultGrid input[type='checkbox']:checked").each(function(){
		if($(this).val() != "on")
		{
		checkedItem += $(this).val().split(",")[0]+','+$(this).val().split(",")[1] +"~";
		}
	});
	if(documentId == '')
	{
	$('#menuFlag').val("true");
	}
else
	{
	$('#menuFlag').val("false");
	}
	$('#checkedDocumentId').val(documentId);
	$('#checkedDocumentType').val(docType);
	var jqxhr = $.ajax(
			{
				url : $("#contextPathSession").val()+"/GetContent.jsp?action=checkDocExits&documentId="+documentId+"&documentType="+docType+"&jspName="+$('#presentFolderId').val(),
				type : 'POST',
				data: {checkedItem :  checkedItem },
				success : function(data) 
				{
					if(data == 'FilenotFound')
					{	
						removePageGreyOut();
						var _message= "The selected file/folder has been deleted.";
						var folderId = $js("#leftTree").jstree("get_selected");
						openFolder(folderId, "", "leftTree", null, null, null, null, null);
						$(".messagediv").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
				  		$(".messagediv").addClass("failed");
				  		$(".messagediv").show();
					}
					else
					{
						$('.messagedivover').empty();
						$('.messagedivover').hide();
						$('.error').empty();
						$(".overlay").launchOverlay($(".alert-box-restore"),$(".exit-panel"), "450px", "225px",null);
					}
				},
				error : function(data, textStatus, errorThrown) {
				},
				complete : function() {
				}
			});
	
}
$(document)
		.ready(
				function() {
					$(".formcontainer.formcontainer-align").removeClass("formcontainer formcontainer-align");
					$('#leftTree').click(function(){
						$js("#selectOrgnization").jstree("deselect_all");
					});
					if((loggedInUserOrgType == 'agency_org' || loggedInUserOrgType == 'provider_org') && $("#manageOrganization").length > 0){
						$('#parentFlag').val("parentflag");
						$(".tabularWrapper").addClass("tabularWrapperDocumentVaultGreen");
					}
					if($('#manageTreeOrgId').length)
					{
						tree($('#hdnopenTreeAjaxVar').val(), 'leftTree', 'selectedfolderid', "DocumentVault", $('#manageTreeOrgId').val(),$('#manageTreeOrgId').val());
					}
					else
					{
						tree($('#hdnopenTreeAjaxVar').val(), 'leftTree', 'selectedfolderid', "DocumentVault",'','');
					}
					if ($('#orgPresent').length) {
						 $('#selectOrgnization').show();
						 tree($('#hdnopenTreeAjaxVar').val(), 'selectOrgnization', 'selectedfolderid', '',"tree","manageTree");
					}
					
					$('#restore').click(
							function() {
								$('.messagedivover').empty();
								$('.messagedivover').hide();
								$('.error').empty();
								$(".overlay").launchOverlay(
										$(".alert-box-restore"),
										$(".exit-panel"), "450px", "450px",
										null);
							});

					$('#restoreButton')
							.click(
									function() {
										ajaxSubmitParallelProcessingMain(provFormAction + '&submit_action=restore', "restore", 3000, "restoreCallBack");
									});
					
					// Start Sharing
					$('#shareDoc').click(
							function() {
								pageGreyOut();
								shareDocument(provFormAction);
								var options = {
									success : function(responseText, statusText, xhr) {
										var $response = $(responseText);
										var data = $response.contents().find(".overlaycontent");
										$("#tab3").empty();
										$("#tab4").empty();
										$("#tab5").empty();
										$("#tab6").empty();
										if (data != null && data != '') {
											$("#tab3").html(data.detach());
										}
										$("#sharelabel").html("- Step 1");
										$("#overlayedJSPContent").html($response);
										$('#sharewiz').removeClass('wizardUlStep1').removeClass(
												'wizardUlStep2').removeClass('wizardUlStep3')
												.removeClass('wizardUlStep4').addClass(
														'wizardUlStep1');
										$(".overlay").launchOverlay($(".alert-box-sharedoc"),
												$(".exit-panel"), "850px", "auto", "onReady");
										removePageGreyOut();
									},
									error : function(xhr, ajaxOptions, thrownError) {
										showErrorMessagePopup();
										removePageGreyOut();
									}
								};
								$(document.provform).ajaxSubmit(options);
								return false;
							});

					$('#deleteDoc').click(function() {
						var folderId = $js("#leftTree").jstree("get_selected");
						$('#presentFolderId').val(folderId);
						ajaxSubmitParallelProcessingMain(provFormAction + '&submit_action=delete', "delete", 3000);
					});

					// Added for Move functionality
					$('#movebutton')
							.click(
									function() {
										if($js("#moveTree").jstree("get_selected") != 0)
										{
											var moveFlag = true;
											var _childToMove=$('#childToMove').val();
											
											var elementsToCheck = new Array();
											if(_childToMove==""){
												$("#documentVaultGrid input[type='checkbox']:checked").each(function(){
													elementsToCheck.push($(this).val().split(",")[0]);
												});
											}else{
												elementsToCheck.push(_childToMove);
											}
											var targetPathToMove = $js("#moveTree").jstree().get_path($js("#moveTree").jstree("get_selected")[0],"/",true) ;
											$(elementsToCheck).each(function(){
												var sourcePath = $js("#moveTree").jstree().get_path(this,"/",true) ;
												var sourceParentPath = $js("#moveTree").jstree().get_path($js("#moveTree").jstree().get_parent(this),"/",true) ;
												
												if(sourcePath == targetPathToMove
														|| targetPathToMove.indexOf(sourcePath) == 0 
														|| targetPathToMove == sourceParentPath){
													$('#messagedivovermove').html("! You selected an invalid folder, please select another folder."+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivovermove', this)\" />");
													$('#messagedivovermove').show();
													moveFlag = false;
													return false;
												}
											});
											if(moveFlag)
											{
												ajaxSubmitParallelProcessingMain(provFormAction
														+ '&submit_action=move&checkedDocumentId='
														+ $('#checkedDocumentId').val()
														+ '&checkedDocumentType='
														+ $('#checkedDocumentType').val(), "move", 3000);
											}
										}
									else{
										
										$('#messagedivovermove').html("! You must select a folder"+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivovermove', this)\" />");
										$('#messagedivovermove').show();
										moveFlag = false;
										return false;
									}});
					// End
					$('#cancel2')
							.click(
									function() {
										displayForm.action = formAction
												+ '&submit_action=cancelrequest';
										var options = {
											success : function(responseText,
													statusText, xhr) {
												$(".overlay").closeOverlay();
												$("#tab1").html("");
												$("#tab2").html("");
												$("#tabnew").html("");
												removePageGreyOut();
											},
											error : function(xhr, ajaxOptions,
													thrownError) {
												showErrorMessagePopup();
												removePageGreyOut();
											}
										};
										pageGreyOut();
										$('ul')
												.removeClass(
														'ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all ui-state-hover ui-tabs-nav');
										$('#step1').removeClass('default')
												.addClass('active');
										$('#step2').removeClass('active');
										$('#step3').removeClass(
												'activeLast active').addClass(
												'last');
										$(displayForm).ajaxSubmit(options);
										return false;
									});

					$('#cancelButton,#cancelOrgButton,#editViewCloseButtonDoc')
					
							.click(function() {
						
								$('.autocomplete').hide();
								close();
								checkOpacity();
							});
					
					// Sharing Status

					// This will execute when upload button is clicked
					$('#uploadDoc')
							.click(
									function() {
										pageGreyOut();
										uploadDocument(provFormAction);
										document.provform.action = provFormAction
												+ "&submit_action=documentupload";

										var options = {
											success : function(responseText,
													statusText, xhr) {
												var $response = $(responseText);
												var data = $response
														.contents()
														.find(".overlaycontent");
												$("#tab1").empty();
												$("#tabnew").empty();
												$("#tab2").empty();
												if (data != null || data != '') {
													$("#tab1").html(
															data.detach());
												}
												$("#overlayedJSPContent").html(
														$response);
												$(".overlay")
														.launchOverlay(
																$(".alert-box-upload"),
																$(".exit-panel.upload-exit"),
																"750px", null,
																"onReady");
												$('#step1').removeClass().addClass('active').css({'margin-left':'10px'});
												$('#step2').removeClass().css({'padding':'0 16px'});
												$('#step3').removeClass().addClass('last').css({'margin-left':'0px','padding-left':'0px','padding-right':'0px'});
												removePageGreyOut();
											},
											error : function(xhr, ajaxOptions,
													thrownError) {
												showErrorMessagePopup();
												removePageGreyOut();
											}
										};
										$(document.provform)
												.ajaxSubmit(options);
										return false;
									});

				
				});
/**
 * This function is called when a file or folder is shared.
 **/
function shareEntity() {
	pageGreyOut();
	document.provform.action = provFormAction + "submit_action=shareStep1";
	var options = {
		success : function(responseText, statusText, xhr) {
			var $response = $(responseText);
			var data = $response.contents().find(".overlaycontent");
			$("#tab3").empty();
			$("#tab4").empty();
			$("#tab5").empty();
			$("#tab6").empty();
			if (data != null && data != '') {
				$("#tab3").html(data.detach());
			}
			$("#sharelabel").html("- Step 1");
			$("#overlayedJSPContent").html($response);
			$('#sharewiz').removeClass('wizardUlStep1').removeClass(
					'wizardUlStep2').removeClass('wizardUlStep3').removeClass(
					'wizardUlStep4').addClass('wizardUlStep1');
			$(".overlay").launchOverlay($(".alert-box-sharedoc"),
					$(".exit-panel"), "850px", "550px", "onReady");
			removePageGreyOut();
		},
		error : function(xhr, ajaxOptions, thrownError) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	};
	$(document.provform).ajaxSubmit(options);
	return false;
};
/**
 * This function is called to perform validation when you open a folder.
 **/
function openFolder(folderId, folderName, divId, ManageOrgFlag, parentFlag,fromSelectNode,
		organizationId, organizationType) 
{
	var selectedFolderId ;
	$("#clickedSearch").val("");
	if($js("#leftTree").jstree(true))
	{
		selectedFolderId = $js("#leftTree").jstree().get_parent($js("#leftTree").jstree("get_selected"));
		
	}
	else
	{
		selectedFolderId = $js("#selectOrgnization").jstree().get_parent($js("#selectOrgnization").jstree("get_selected"));
	}
	var jqxhr = $.ajax(
	{
		url : $("#contextPathSession").val()+"/GetContent.jsp?action=checkDocExits&documentId="+folderId+"&jspName="+folderId,
		type : 'POST',
		success : function(data) 
		{
			if(data == 'FilenotFound')
			{	
				removePageGreyOut();
				var _message= "The selected folder has been deleted.";
				openFolderFinal(selectedFolderId,'',divId,ManageOrgFlag, parentFlag,fromSelectNode,organizationId,organizationType);
				$(".messagediv").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
		  		$(".messagediv").addClass("failed");
		  		$(".messagediv").show();
				
			}
			else
			{
				openFolderFinal(folderId, folderName, divId, ManageOrgFlag, parentFlag,fromSelectNode,
						organizationId, organizationType);
			}
		},
		error : function(data, textStatus, errorThrown) {
		},
		complete : function() {
		}
	});
	

	
}

/**
 * This function is called when above validation is successful, on open folder.
 **/
function openFolderFinal(folderId, folderName, divId, ManageOrgFlag, parentFlag,fromSelectNode,
		organizationId, organizationType)
{
	var appendToURLTypeHead = "";
	var parentId = 'child';
	if (divId == 'selectOrgnization') {
		$("#removeList").show();
		$('.messageDiv').hide();
		$("#searchDoc,#recyclebindiv,#findOrgDocbtn, .hideShow, #downloadAll").hide();
		$("#OrgNameHeader, #findOrgDocbtn").show();
		$('#removeList').show();
		if($('#findToolTip').val()=="agency_org"){
			$(".searchdocbutton").attr("title", "Search for specific shared documents from this organization");
			$(".searchdocbutton .foldercolor").attr("title", "Search for specific shared documents from this organization");
		}
		else
		{
			$(".searchdocbutton").attr("title", "Search for specific documents from this organization");
			$(".searchdocbutton .foldercolor").attr("title", "Search for specific documents from this organization");	
		}	
		$js("#leftTree").jstree("deselect_all");
		// Added for 7570
		var selectOrgnizationTreeSelected = $js("#selectOrgnization").jstree("get_selected");
		var selectedOrgType = '';
		var selectedOrgId = '';
		if(selectOrgnizationTreeSelected.length > 0 && $js("#selectOrgnization").jstree(true)){
			selectedOrgType = $js("#selectOrgnization").jstree(true).get_node(selectOrgnizationTreeSelected).data.organizationType;
			selectedOrgId = $js("#selectOrgnization").jstree(true).get_node(selectOrgnizationTreeSelected).data.organizationId;
		}
		appendToURLTypeHead = "&orgType="+ selectedOrgType + "&userOrgId="+ selectedOrgId;
		$('#selectedOrgTypeForLinkage').val(selectedOrgType);
	}
	else if (divId == 'leftTree') {
		$('#searchicon').attr('title','Search for specific documents in your vault');
		$("#removeList").hide();
		$('#OrgNameHeader').hide();
	}
	
		$('#searchform #procurementTitle').autoCompleteOptionSave("getSuggestions").serviceUrl = $("#contextPathSession").val()
			+ "/GetContent.jsp?&isProcurementTitle=true" + appendToURLTypeHead;
		$('#searchform #invoiceNum').autoCompleteOptionSave("getSuggestions").serviceUrl = $("#contextPathSession").val()
		+ "/GetContent.jsp?&getInvoiceDetails=true" + appendToURLTypeHead; 
	if (divId == 'selectOrgnization' && parentFlag == '#') {
		$('#OrgNameHeader').show();
		if (folderName.length > 25) {
			$('#OrgNameHeader').text(folderName.substring(0, 25) + '...');
		} else {
			$('#OrgNameHeader').text(folderName);
		}

	}
	$('#RecyclebinFlag').val('');
	if (parentFlag == '#') {
		parentId = 'root';
		parentFlag = 'parentflag';
	}
	pageGreyOut();
	var _temp;
	if (folderId.indexOf("RecycleBin") >= 0) {
		$('#searchicon').attr('title','Search for documents deleted from your vault');
		$('#RecyclebinFlag').val('RecycleBin');
		$('#RecyclebinId').val(folderId);
	}
	if (folderId.indexOf("~") == -1) {
		$('#folderId').val(folderId);
		document.provform.action = provFormAction //+ "&folderName=" + folderName
				+ "&folderId=" + folderId + "&submit_action=openFolder&divId="
				+ divId + "&action=documentVault&ManageOrgFlag="
				+ ManageOrgFlag + "&parentFlag=" + parentFlag+ "&parentId=" + parentId
				+ "&organizationId=" + organizationId + "&organizationType="
				+ organizationType;
		$("#headerClick").val("false");

	} else {
		_temp = folderId.split("~");
		$('#folderId').val(_temp[0]);
		document.provform.action = provFormAction //+ "&folderName=" + folderName
				+ "&folderId=" + folderId
				+ "&submit_action=openFolder&action=documentVault&ManageOrgFlag="
				+ ManageOrgFlag + "&organizationId=" + organizationId;
	}
		var options = {
			success : function(responseText, statusText, xhr) {
				removePageGreyOut();
				var data = $(responseText);
				$("#myform .tabularWrapper").replaceWith(data.find(".tabularWrapper"));

				if (divId == 'selectOrgnization') {
					if(!$(".tabularWrapperDocumentVault").hasClass("tabularWrapperDocumentVaultGreen"))
						$(".tabularWrapperDocumentVault").addClass("tabularWrapperDocumentVaultGreen");
				}
				else if(loggedInUserOrgType!='city_org' && $("#manageOrganization").length > 0){
					if(!$(".tabularWrapperDocumentVault").hasClass("tabularWrapperDocumentVaultGreen"))
						$(".tabularWrapperDocumentVault").addClass("tabularWrapperDocumentVaultGreen");
				}
				else if (divId == 'leftTree' && $("#manageOrganization").length == 0) {
					$(".tabularWrapperDocumentVault").removeClass("tabularWrapperDocumentVaultGreen");
				}
				$js('#'+divId).jstree("select_node", folderId);
				var _message = $(responseText).find('#message').val();
				var _messageType = $(responseText).find('#messageType').val();
				if (_message != "") {
					$(".messagediv")
							.html(
									_message
											+ "<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
					$(".messagediv").addClass(_messageType);
					$(".messagediv").show();
				} 
				if ($("#hideEmptyRecycleBin").size() > 0
						&& parseInt($("#hideEmptyRecycleBin").val()) == 0) {
					$("#emptyBin").hide();
				} else if ($("#hideEmptyRecycleBin").size() > 0
						&& parseInt($("#hideEmptyRecycleBin").val()) > 0) {
					$("#emptyBin").show();
				}
				if(fromSelectNode){
					$js('#leftTree').jstree('refresh');
				}else{
					removePageGreyOut();
				}
			}
		};
		$(document.provform).ajaxSubmit(options);
		
	return false;
	}

/**
 * This function is called to view information.
 **/
function viewInfo(documentId, docType, docCat,OrgType, OrgId,orgFlag) {	
	$('#checkedDocumentType').val(docType);
	$('#checkedDocumentId').val(documentId);
	pageGreyOut();
	var CustomOrg;
	$('#editFolderNameText').hide();
	$('#docTypeHidden').val(docType);
	$('#docCatHidden').val(docCat);
	if($('#manageTreeOrgId').length || $('#ManageTreeOrgType').length)
	{
		CustomOrg = $('#ManageTreeOrgType').val();
		if(null != CustomOrg && CustomOrg != "")
		{
			$('#sharedPageOrg').val(CustomOrg);
		}
	}
	
	if (docType == "null")
	{
		viewFolderInformation(documentId, docType, docCat,OrgType, OrgId,orgFlag);
	} 
	else 
	{
		var selectedNode = $js('#leftTree').jstree('get_selected');
		var jqxhr = $.ajax(
		{
		url : $("#contextPathSession").val()+"/GetContent.jsp?action=checkDocExits&documentId="+documentId+"&documentType="+docType+"&jspName="+selectedNode,
		type : 'POST',
		success : function(data) 
		{
			if(data == 'FilenotFound')
			{	
				removePageGreyOut();
				var _message= "The selected file has been deleted.";
				
				if($('#presentFolderId').val().startsWith("RecycleBin") && selectedNode.length > 0)
					{
					var folderId = $js("#leftTree").jstree("get_selected");
					openFolder(folderId, "", "leftTree", null, null, null, null, null);
					}
				else
					{
					openRootNodeInTree('leftTree');
					}
				$(".messagediv").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
		  		$(".messagediv").addClass("failed");
		  		$(".messagediv").show();
			}
			else
				{
				
				document.provform.action = provFormAction + "&documentId=" + documentId
				+ "&docType=" + docType + "&docCategory="+docCat+"&organizationType="+OrgType+"&organizationId="+OrgId+"&&submit_action=viewDocumentInfo&sharedPageOrg="+orgFlag+"&action=documentVault";
				var options = {
			success : function(responseText, statusText, xhr) {
				removePageGreyOut();
				var data = $(responseText);
				var _message = $(responseText).find('#message').val();
				var _messageType = $(responseText).find('#messageType').val();
				if (_message != "") {
					$(".messagediv").html(_message+ "<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
					$(".messagediv").addClass(_messageType);
					
				}
				if(_messageType != ""){
					$(".messagediv").show();
				}else{
				$(".alert-box-viewFolder").replaceWith(
						data.find(".alert-box-viewFolder"));
				if(orgFlag == "false")
				{
				$('#documentlocation').hide();
				}
				if(orgFlag == "false" && OrgType =="agency_org")
				{
				$('#documentlocation').show();
				}
				if(orgFlag == "false" && OrgType =="provider_org")
				{
				$('#documentlocation').show();
				}
				
				$(".overlay").launchOverlay($(".alert-box-viewFolder"),
						$(".exit-panel,#editViewCloseButtonDoc"), "565px", null, "onReady");
				
				 			$('#periodcoveredfrommonth').prev().attr('style' , 'width : 35px !important');
				        	$('#periodcoveredfromyear').prev().attr('style' , 'width : 35px !important');
				        	$('#periodcoveredtomonth').prev().attr('style' , 'width : 35px !important');
				        	$('#periodcoveredtoyear').prev().attr('style' , 'width : 35px !important');
				        	$('#periodcoveredfrommonth').attr('style' , 'width : 50px !important');
				        	$('#periodcoveredfromyear').attr('style' , 'width : 35px !important');
				        	$('#periodcoveredtomonth').attr('style' , 'width : 50px !important');
				        	$('#periodcoveredtoyear').attr('style' , 'width : 35px !important');
				        	$('#periodcoveredfromyear').attr('readonly' , 'readonly');
				        	$('#periodcoveredtoyear').attr('readonly' , 'readonly');
				        	var a=$('.documentLocationPath').text().trim();
							b='<div style="height: 15px;" ></div>';
							$('.documentLocationPath').html(b);
							$('.documentLocationPath div').html(a);
				    		var docShareListOriginal=$('.docShareWithList').text().match(/.{1,50}/g);
				    		var docnewShareList='';
				    		if(docShareListOriginal!=null){
				    		for(var i = 0; i< docShareListOriginal.length; i++){
				     			if(docShareListOriginal[i].toString()!=null){
				     				docnewShareList=docnewShareList+'<p>'+docShareListOriginal[i].toString()+'</p>';
				    	    			}
				    		}
				    		$('.docShareWithList').html(docnewShareList);
				    		}
				}
			}
		};
		$(document.provform).ajaxSubmit(options);
				}
		},
		});
		
	}
}
/**
 * This method is invoked when user wants to view folder information
 **/
function viewFolderInformation(documentId, docType, docCat,OrgType, OrgId,orgFlag)
{
	var jqxhr = $.ajax(
			{
	url : $("#contextPathSession").val()+"/GetContent.jsp?action=checkDocExits&documentId="+documentId+"&documentType="+docType+"&jspName="+$('#presentFolderId').val(),
	type : 'POST',
	success : function(data) 
	{
		if(data == 'FilenotFound')
		{	
			removePageGreyOut();
			var _message= "The selected folder has been deleted.";
			if($('#presentFolderId').val().startsWith("RecycleBin"))
			{
			var folderId = $js("#leftTree").jstree("get_selected");
			openFolder(folderId, "", "leftTree", null, null, null, null, null);
			}
			else
			{
			openRootNodeInTree('leftTree');
			}
			//openRootNodeInTree('leftTree');
			$(".messagediv").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
	  		$(".messagediv").addClass("failed");
	  		$(".messagediv").show();
		}
		else
			{
	document.provform.action = provFormAction + "&documentId=" + documentId
			+ "&docType=" + docType + "&docCategory="+docCat+"&organizationType="+OrgType+"&organizationId="+OrgId+"&submit_action=viewDocumentInfo&sharedPageOrg="+orgFlag+"&action=documentVault&sharedPageOrgType="+$('#manageTreeOrgId').val();
	var options = {
		success : function(responseText, statusText, xhr) {
			removePageGreyOut();
			var data = $(responseText);
			var _message = $(responseText).find('#message').val();
			var _messageType = $(responseText).find('#messageType').val();
			if (_message != "") {
				$(".messagediv")
						.html(
								_message
										+ "<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
				$(".messagediv").addClass(_messageType);
				
			}
			if(_messageType != ""){
				$(".messagediv").show();
			}else{
				$(".alert-box-viewFolderProperties").replaceWith(
						data.find(".alert-box-viewFolderProperties"));
				if(orgFlag == "false")
				{
					$('#folderlocation').hide();
				}
				if(orgFlag == "false" && (OrgType =="agency_org" ||  OrgType =="provider_org"))
				{
					$('#folderlocation').show();
				}
				$(".overlay").launchOverlay(
						$(".alert-box-viewFolderProperties"), $(".exit-panel ,#editViewCloseButton"),
						"460px", null, "onReady");
				var a=$('.folderLocationPath').text().trim();
				b='<div style="height: 15px;" ></div>';
				$('.folderLocationPath').html(b);
				$('.folderLocationPath div').html(a);
	    		var folderShareListOriginal=$('.folderShareWithList').text().match(/.{1,50}/g);
	    		var foldernewShareList='';
	    		if(folderShareListOriginal!=null){
		    		for(var i = 0; i< folderShareListOriginal.length; i++){
		     			if(folderShareListOriginal[i].toString()!=null){
		     				foldernewShareList=foldernewShareList+'<p>'+folderShareListOriginal[i].toString()+'</p>';
		    	    	}
		    		}
		    		$('.folderShareWithList').html(foldernewShareList);
	    		}
			}
		}
	};
	$(document.provform).ajaxSubmit(options);
			}
	},
	});
}
/**
 * This function is called for edit properties of folder.
 **/
function hideSaveCancelFolder() {
	$('#editFolderName,#editFolderInfo').show();
	$('#editFolderNameText,#editViewCancelButton,#editViewSaveButton,#messagedivoveredit').hide();
	$('span.error label').html('');
}
/**
 * This function is called for edit properties of file.
 **/
function hideSaveCancelDoc() {
	$('.customProp').hide();
	$('.radioButtonHelp').attr('disabled',true);
	$('.customPropformfield').show();
	$('#editDocName,#editDocInfo').show();
	$('#editDocNameText').hide();
	$('#editViewCancelButtonDoc').hide();
	$('#editViewSaveButtonDoc').hide();
	$('.imgClass #cal').hide();
	$('div #messagedivover').hide();
	$('span.error label').html('');
}
/**
 * This function is called to close overlay.
 **/
function close() {
	$(".overlay").closeOverlay();
	removePageGreyOut();
	return false;
}
/**
 * This function is called for edit properties of folder.
 **/
function searchDoc() {
	if($('#listCityAppSummary ul li').length >0 ){
		$('#addsharedmargin').css({'margin-top': '80px'});
	}
	else{
		$('#addsharedmargin').css({'margin-top': '75px'});
	}
	if ($("#RecyclebinFlag").val() != 'RecycleBin') {
		 if(loggedInUserOrgType == 'city_org' || ((loggedInUserOrgType == 'agency_org' || loggedInUserOrgType == 'provider_org') && $("#manageOrganization").length == 0)){
			 $("#searchDoc").slideToggle();
			 $("#findorgdocform").hide();
		 }
		 else if((loggedInUserOrgType == 'provider_org' || loggedInUserOrgType == 'agency_org') && $("#manageOrganization").length > 0){
			 $('#findDoc').slideToggle();
		 }
	} else {
		$("#recyclebindiv").slideToggle();
	}
	searchFieldsUpdate();
}
/**
 * This function is called to disable Share With field in search.
 * */
function disableShareWith(object) {
	if (object.value == "unshared" || object.value == "") {
		$("#sharedWith").val('');
		$("#sharedWith").prop('disabled', true);
		$('#sharedWitherror').text("");
	} else {
		$("#sharedWith").prop('disabled', false);
	}
}
/**
 * This function is called to remove error message.
 **/
function eraseError()
{
	if(document.getElementById("docName").value == "")
		{
		$("#arpit").val('');
		}
}
/**
 * Added for Find Org Document Button click
 **/
function addList() {

	var e = document.getElementById("dropDownId");
	var strUser = e.options[e.selectedIndex].text;
	$("div#itemlist ul").append('<li><a href="#">' + strUser + '</a></li>');
	if ($("div#itemlist ul li").length > 0) {
		$("#removeList").show();
	}
	close();
}
/**
 * This function is called when select organization is clicked.
 **/
function orgOpen() {
	pageGreyOut();
	$('#checkOrg').val('false');
	$('#SubmitButton').prop('disabled','disabled');
	$('#provName').val('');
	$('.messageDiv').hide();
	$(".overlay").launchOverlay($(".overLay1"), $(".exit-panel"), "391px",
			null, "onReady");
	removePageGreyOut();
	// Combo box comment code
};

// End
/**
 * Added for Find Org Docuyment Button click 
 **/
$(function() {
	
	$('.object1').on("click", function() {
		$("#searchDoc,#recyclebindiv,#vaultheader").hide();
		$("#removeList").show();
		$("#findOrgDocbtn").show();
	});
	var optionsdownload = {
			success : function(responseText, statusText, xhr) {
				var _message = $(responseText).find('#message').val();
				var _messageType = $(responseText).find('#messageType').val();
				  $(".messagediv").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
		 			$(".messagediv").addClass(_messageType);
		 			$(".messagediv").show();
				removePageGreyOut();
				;},
			error : function(xhr, ajaxOptions, thrownError) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		};
	$("#downloadAll").on("click", function(){
		pageGreyOut();
		document.findorgdocform.action = findOrgDocForm + "&submit_action=downloadAll";
		$(document.findorgdocform).ajaxSubmit(optionsdownload);
	});
	$("#findOrgDoc").on("click", function() {
		if($("#findorgdocform #findDoc").is(":visible")){
			$("#findorgdocform #findDoc").hide();
		}else{
			if(!$("#findOrgDoc").hasClass("selected")){
				pageGreyOut();
				$.ajax({
			    	type: "POST",
				    url: $('#findSharedDocsUrl').val(),
				    success:function(data)
				    {
				    	$("#myform .tabularWrapper").replaceWith($(data).find(".tabularWrapper"));
				    	removePageGreyOut();
				    }
			 });
			}
			$("#findorgdocform, #findOrgDocbtn, #findshareddocform").show();
	    	$("#findorgdocform #findDoc, #findshareddocform #findDoc").slideToggle();
			$("#OrgNameHeader, #removeList, #searchDoc, #recyclebindiv, #vaultheader, #downloadAll").hide();
			$("#checkOrg").val('true');
			$("#findOrgDoc").addClass("selected");
			$js("#leftTree, #selectOrgnization").jstree("deselect_all");
			var toolTip = "Search for specific documents across all organizations in the system.";
			if(loggedInUserOrgType == 'agency_org'){
				toolTip = "Search for specific shared documents across all organizations that have shared with you";
			}
			$(".searchdocbutton").attr("title", toolTip);
			$(".searchdocbutton .foldercolor").attr("title", toolTip);
		}
	});
	
	$("#removeList").on("click", function() {
		// Changes for 7577 starts
		var selectOrgnizationTreeSelected = $js("#selectOrgnization").jstree("get_selected");
		var orgId = $js("#selectOrgnization").jstree(true).get_node(selectOrgnizationTreeSelected).data.organizationId;
		var orgType = $js("#selectOrgnization").jstree(true).get_node(selectOrgnizationTreeSelected).data.organizationType;
		var urlArg = orgId + '~' + orgType;
		// Changes for 7577 ends
		pageGreyOut();
		$('#selectOrgnization').html('');
		 $.ajax({
		    	type: "POST",
		    	data: {userOrg:  urlArg },
			    url: $('#removeSession').val(),
			    success:function(data)
			    {
			    	removePageGreyOut();
			    	$(".object1").html('');
			    	window.location.reload();
			    }
			});
	});
	
	$(".searchdocbutton").on("click", function() {
		var mainTreeSelected = $js("#leftTree").jstree("get_selected");
		var selectOrgnizationTreeSelected = $js("#selectOrgnization").jstree("get_selected");
		if(mainTreeSelected.length > 0){
			$("#findshareddocform #findDoc").hide();
			$("#findorgdocform").show(); 
		 }else if(selectOrgnizationTreeSelected.length > 0 && $js("#selectOrgnization").jstree(true)){
			if(loggedInUserOrgType != 'city_org'){
				$("#findshareddocform").show();
				$("#findshareddocform #findDoc").slideToggle();
			}else{
			// Adding below if-else condition for Emergency Release 4.0.2
				var selectedOrg ;
				if(typeof selectOrgnizationTreeSelected == 'object')
					{
					selectedOrg = selectOrgnizationTreeSelected[0];
					}
				else
					{
					selectedOrg = selectOrgnizationTreeSelected;
					}
				if(selectedOrg.indexOf("RecycleBin") != -1)
					{
					
					$("#deleteform").show();
					$("#deleteform #recyclebindiv").slideToggle();
					}
				else
					{
					$("#searchform").show();
					$("#searchform #searchDoc").slideToggle();
					}
				
			}
		 }else if($("#findOrgDoc").hasClass("selected")){
			 $("#findOrgDoc").click();
			 if(loggedInUserOrgType == 'city_org'){
			 if(parseInt($('#documentListForDownloadAll').val()) == 0)
				{
				 $("#downloadAll").hide();
				 }
			 else
				 {
				 $("#downloadAll").show();
				 }
			 }
		 }
		searchFieldsUpdate();
	});
});
/* End of function */
/**
 * This function is called to perform search on Linked Entity.
 **/
function getEntityLinked(fiscalYearId, BudgetTypeId,bussId, bussAppId,appStatus, orgId, entityName,ctNum, entityId, ParentId, organizationId,statusIdProcurement,contractId,appId,serviceAppId )
{
	
	if(entityName == 'Proposal')
		{
		window.location.href = "/HHSPortal/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_procurement&_nfls=false" +
				"&app_menu_name=header_procurement&resetSessionProcurement=true&action=rfpRelease&render_action=" +
				"procurementProposalDocumentList&midLevelFromRequest=ProposalDocuments&topLevelFromRequest=ProposalSummary" +
				"&procurementId="+ParentId+"&proposalId="+entityId+"&_windowLabel=portletInstance_38&_urlType=render";
		}
	else if(entityName == 'Procurement')
		{
		window.location.href = "/HHSPortal/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_procurement&_nfls=false" +
		"&app_menu_name=header_procurement&resetSessionProcurement=true&action=rfpRelease&render_action=" +
		"displayRFPDocumentList&midLevelFromRequest=RFPDocuments&topLevelFromRequest=ProcurementInformation" +
		"&procurementId="+entityId+"&_windowLabel=portletInstance_38&_urlType=render";
		}
	else if(entityName == 'Award' && orgId == 'provider_org')
	{
		window.location.href = "/HHSPortal/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_procurement&_nfls=false" +
		"&app_menu_name=header_procurement&resetSessionProcurement=true&action=selectionDetail&render_action=" +
		"viewSelectionDetails&midLevelFromRequest=SelectionDetailsScreen&topLevelFromRequest=SelectionDetails" +
		"&procurementId="+entityId+"&evaluationPoolMappingId="+ParentId+"&_windowLabel=portletInstance_38&_urlType=render";
	}
	
	else if(entityName == 'Agency Award')
	{
		window.location.href = "/HHSPortal/portal/hhsweb.portal?_nfpb=true&_st=&_windowLabel=portletInstance_38" +
				"&_urlType=render&wlpportletInstance_38_action=selectionDetail&wlpportletInstance_38_render_action=viewSelectionDetails" +
				"&procurementId="+entityId+"&evaluationPoolMappingId="+ParentId+"&organizationId="+organizationId+"&isFinancials=true" +
				"&asProcStatus="+statusIdProcurement+"&contractId="+contractId+"&removeMenu=asdas&_pageLabel=portlet_hhsweb_portal_page_procurement";
	}
	
	else if(entityName == 'Invoice')
	{
		window.location.href = "/HHSPortal/portal/hhsweb.portal?_nfpb=true&_st=&_windowLabel=portletInstance_37" +
				"&_urlType=action&wlpportletInstance_37_action=invoiceListAction&wlpportletInstance_37_viewInvoice=contractInvoiceScreen" +
				"&invoiceId="+entityId+"&_pageLabel=portlet_hhsweb_portal_page_financials";
	}
	//Added for Release 6 Returned payment review
	else if(entityName == 'Budget' || entityName == 'Returned Payment')
	{
		window.location.href = "/HHSPortal/portal/hhsweb.portal?_nfpb=true&_st=&_windowLabel=portletInstance_37" +
				"&_urlType=action&wlpportletInstance_37_submit_action=viewContractBudget&wlpportletInstance_37_action=budgetListAction" +
				"&budgetId="+entityId+"&contractId="+contractId+"&ctId="+ctNum+"&fiscalYearId="+fiscalYearId+"&budgetType="+BudgetTypeId +
				"&loadModificationFirst=false&_pageLabel=portlet_hhsweb_portal_page_financials";
	}
	//Added for Release 6 Returned payment end
	else if(entityName == 'Business Application')
	{
		window.location.href = "/HHSPortal/portal/hhsweb.portal?_nfpb=true&_st=&_windowLabel=businesssummary_1" +
				"&_urlType=action&wlpbusinesssummary_1_bussAppId="+bussId+"&wlpbusinesssummary_1_appId="+bussAppId+
				"&wlpbusinesssummary_1_applicationStatus="+appStatus+"&wlpbusinesssummary_1_applicationType=business" +
				"&wlpbusinesssummary_1_headerPostSubmitionBusiness=businessapplication&_pageLabel=portlet_hhsweb_portal_page_business_summary";
	}
	else if(entityName == 'Service Application')
	{
		window.location.href = "/HHSPortal/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_service_post&_nfls=false&"+
							   "section=servicessummary&subsection=summary&next_action=checkForService&headerPostSubmitionService=true&"+
							   "business_app_id="+bussAppId+"&bussAppStatus="+appStatus+"&loReadOnly=true&applicationId="+appId+
							   "&applicationType=service&displayHistory=displayHistory&service_app_id="+serviceAppId+"&elementId="+bussId+"&app_menu_name=header_application";
	}
}
/**
 * This function is called to perform linkage search.
 **/
function linkDocTo() {
	$('#entity_type').val(document.getElementById("linked").value);
	$("#procurementTitle").parent().find(".error").html("");
	$("#awardepinTitle").parent().find(".error").html("");
	$("#invoiceNum").parent().find(".error").html("");
	$("#submittedfrom1").parent().find(".error").html("");
	$("#submittedTo1").parent().find(".error").html("");
	$("#paymentnoTitle").parent().find(".error").html("");
	$("#amendmentepinTitle").parent().find(".error").html("");
	$("#contractAwardEpinDiv").parent().find(".error").html("");
	$("#procTitleDiv").hide();
	$("#cTNumDiv").hide();
	$("#invoiceDiv").hide();
	$("#submitDateRangeFrom").hide();
	$("#submitDateRangeTo").hide();
	$("#awardepindiv").hide();
	$("#paymentNumber").hide();
	$("#amendmentepindiv").hide();
	$("#contractAwardEpinDiv").hide();
	$("#serchLinkedFieldsContainer input").val("");
	if (document.getElementById("linked").value == "Proposal")
	{
		$("#procTitleDiv").show();
	} else if (document.getElementById("linked").value == "Budget") 
	{
		$("#awardepindiv").show();
		
	} else if (document.getElementById("linked").value == "Invoice") {
		
		$("#invoiceDiv").show();
		
	} else if (document.getElementById("linked").value == "Business Application"
			|| document.getElementById("linked").value == "Service Application") {
		
		$("#submitDateRangeFrom").show();
		$("#submitDateRangeTo").show();
		
	} else if(document.getElementById("linked").value =='Contract'
		|| document.getElementById("linked").value =='returnedPayment'){
		$("#contractAwardEpinDiv").show();
		
	}
	else if(document.getElementById("linked").value =='Payment'){
		$("#paymentNumber").show();
		
	}
	else if(document.getElementById("linked").value =='Procurement'){
		$("#procTitleDiv").show();
		
		
	}
	else if (document.getElementById("linked").value == "Filings"
		|| document.getElementById("linked").value == "") 
	{
	
	}
	else if(document.getElementById("linked").value =="amendment"){
		$("#amendmentepindiv").show();
		
	}
	else if(document.getElementById("linked").value =="Award" || document.getElementById("linked").value == "providerAward"){
		$("#procTitleDiv").show();
	}
	else if(document.getElementById("linked").value =="agencyAward"){
		$("#awardepindiv").show();
	}
}
/**
 * This function is called to check linkage status of a document.
 **/
function linkStatus(documentId, docType,contractAccess) {
	
	if (docType == "null") {
		$(document).ready(function() {
			$("a").click(function(event) {
				event.preventDefault();
			});
		});

	} else {
		pageGreyOut();
		$.ajax({

			type : "POST",
			url : $('#checkLinkageParam').val() + "&documentId=" + documentId
					+ "&docType=" + docType + "&contractAccess=" + contractAccess
					+ "&submit_action=linkageInformation",
			success : function(e) {
				removePageGreyOut();
				var data = $(e);
				$(".alert-box-linkDocStatus").replaceWith(
						data.find(".alert-box-linkDocStatus"));
				$(".overlay").launchOverlay($(".alert-box-linkDocStatus"),
						$(".exit-panel, #linkedCancelButton"), "500px");
			}
		});

	}

}
/**
 * This function is called to clear filters in search.
 **/
function clearFilter() {
	$("#docname_city,#doctype_agency,#doctype_city,#docname_agency,#docName,#filtertype,#modifiedfrom,#shared,#linked,#procurementTitle,#cTNum").val('');
	$("#awardepinTitle,#invoiceNum,#submittedfrom1,#submittedTo1,#amendmentepinTitle,#submittedfrom,#submittedTo,#docNameRec,#modifiedfrom2,#modifiedto2,#modifiedfrom3,#modifiedto3,#modifiedfrom4,#modifiedto4,#modifiedfrom5,#modifiedto5,#docTypeRec").val('');
	$("#procTitleDiv").hide();
	$("#awardepindiv").hide();
	$("#amendmentepindiv").hide();
	$("#cTNumDiv").hide();
	$("#invoiceDiv").hide();
	$("#submitDateRangeFrom").hide();
	$("#submitDateRangeTo").hide();
	$("#deleteform,#searchform,#findshareddocform,#findorgdocform").find("span.error").hide();
	$(".checboxcontainer").find('input:checked').attr('checked', false);
	$("#sharedWith").val('');
	$("#sharedWith").attr('disabled',true);
	//Added in R6 for Returned Payment
	$("#contractAwardEpinDiv").hide(); 
	//Added in R6 for Returned Payment:End
}
/**
 * This function is called to clear filters in search.
 **/
function shareEntityInfo(documentId, docType, sharedEntityId) {
	pageGreyOut();
	var checkedItem = "";
	$("#documentVaultGrid input[type='checkbox']:checked").each(function(){
		if($(this).val() != "on")
		{
		checkedItem += $(this).val().split(",")[0]+','+$(this).val().split(",")[1] +"~";
		}
	});
	if(documentId == '')
	{
	$('#menuFlag').val("true");
	}
else
	{
	$('#menuFlag').val("false");
	}
	var shareFlag = true;
	var selectedFolderId ;
	if($js("#leftTree").jstree(true))
	{
		selectedFolderId = $js("#leftTree").jstree().get_parent($js("#leftTree").jstree("get_selected"));
		
	}
	else
	{
		selectedFolderId = $js("#selectOrgnization").jstree().get_parent($js("#selectOrgnization").jstree("get_selected"));
	}
	var jqxhr = $.ajax(
	{
		url : $("#contextPathSession").val()+"/GetContent.jsp?action=checkDocExits&documentId="+documentId+"&documentType="+docType+"&jspName="+$('#presentFolderId').val(),
		type : 'POST',
		data: {checkedItem :  checkedItem },
		success : function(data) 
		{
			if(data == 'FilenotFound')
			{	
				removePageGreyOut();
				var _message= "The selected file/folder has been deleted.";
				openRootNodeInTree('leftTree');
				$(".messagediv").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
		  		$(".messagediv").addClass("failed");
		  		$(".messagediv").show();
				
			}
			else
			{
				if($('#documentVaultGrid input[name="check"]:checked').size() > 0)
				{
					$("td span.red-ex-mark").remove();
					$('#documentVaultGrid input[name="check"]:checked').each(function() {
						var res = (this.value).split(",");
						if(null != res[2] && res[2] != '' && res[2] != 'null' && res[0] != res[2])
						{
							$(".messagediv")
							.html(
							"At least 1 of the selected documents or folders you selected are part of a Shared folder. " +
							"The exclamation mark(s) below indicate the item(s) that cannot be shared. Please de-select these items and try again."
							+ "<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
							$(".messagediv").addClass('failed');
							$(".messagediv").show();
							shareFlag = false;
							$("#documentVaultGrid input[name='check'][value*='"+res[0]+"']").closest("tr").find("td").eq(1).append("<span class='red-ex-mark red-ex-mark-documentVault'>&nbsp;</span>")
							removePageGreyOut();
							//return false;
						}
						
					});
				}
				else if(null != sharedEntityId && sharedEntityId != '' && sharedEntityId != 'undefined' && sharedEntityId != 'null')
				{
					$("td span.red-ex-mark").remove();
					if(null != documentId && documentId != '' && documentId != 'null' && documentId != sharedEntityId)
					{
						$(".messagediv")
						.html(
						"At least 1 of the selected documents or folders you selected are part of a Shared folder. " +
						"The exclamation mark(s) below indicate the item(s) that cannot be un-shared. Please de-select these items and try again."
						+ "<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
						$(".messagediv").addClass('failed');
						$(".messagediv").show();
						shareFlag = false;
						$("#documentVaultGrid input[name='check'][value*='"+documentId+"']").closest("tr").find("td").eq(1).append("<span class='red-ex-mark red-ex-mark-documentVault'>&nbsp;</span>")
					}
				}
				if(shareFlag)
				{
					$('#childToMove').val(documentId);
					$('#checkedDocumentType').val(docType);
					$('#checkedDocumentId').val(documentId);
					var url = $("#contextPathVal").val()+'/GetContent.jsp?lockingFlag=true&nextAction=share';
					$('#myform').attr('action', url);
					var optionsForMove = {
					success : function(responseText,statusText, xhr) {
					if(responseText == '' || responseText == 'true')
					{
						$(".messagediv")
						.html(
						"A transaction is currently in progress for the selected documents. Please try again at a later time."
						+ "<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
						$(".messagediv").addClass('failed');
						$(".messagediv").show();
						$(".overlay")
						.closeOverlay();
						removePageGreyOut();
					}
					else
					{
						if(shareFlag)
							{
								pageGreyOut();
								$('#checkedDocumentType').val(docType);
								shareDocument(provFormAction,documentId);
								var options = {
								success : function(responseText, statusText, xhr) {
								var $response = $(responseText);
								var responses = responseText.split("|");
								if ((responses[1] == "Error" || responses[1] == "Exception")) {
									var _message = responses[3];
									var _messageType = responses[4];
				
									$(".messagediv")
											.html(
													_message
															+ "<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
									$(".messagediv").removeClass("failed passed");
									$(".messagediv").addClass(_messageType);
									$(".messagediv").show();
									$(".overlay").closeOverlay();
									removePageGreyOut();
								}
								else
								{
									var data = $response.contents().find(".overlaycontent");
									$("#tab3").empty();
									$("#tab4").empty();
									$("#tab5").empty();
									$("#tab6").empty();
									if (data != null && data != '') {
									$("#tab3").html(data.detach());
									}
									$("#sharelabel").html("- Step 1");
									$("#overlayedJSPContent").html($response);
									$('#sharewiz').removeClass('wizardUlStep1').removeClass(
									'wizardUlStep2').removeClass('wizardUlStep3').removeClass(
									'wizardUlStep4').addClass('wizardUlStep1');
									$(".overlay").launchOverlay($(".alert-box-sharedoc"),
									$(".exit-panel"), "830px", "onReady");
									removePageGreyOut();
								}
							
							},
							error : function(xhr, ajaxOptions, thrownError) {
							showErrorMessagePopup();
							removePageGreyOut();
							}
							};
							$(document.provform).ajaxSubmit(options);
							return false;
						}
					}
					},
					error : function(xhr, ajaxOptions,
					thrownError) {
					showErrorMessagePopup();
					removePageGreyOut();
					}
					};
					$(document.provform).ajaxSubmit(optionsForMove);
				}
			}
		},
		error : function(data, textStatus, errorThrown) {
		},
		complete : function() {
		}
	});
	}




/**
 * Added For R5
 * This function is called when check all check boxes is clicked.
 */
function checkAll(ele) {
	var checkboxes = document.getElementsByTagName('input');
	if (ele.checked) {

		$("#file_menu").css({
			opacity : 1.0
		});
	
		for ( var i = 0; i < checkboxes.length; i++) {
			if (checkboxes[i].type == 'checkbox') {
				checkboxes[i].checked = true;
			}
		}
	} else {
		$("#file_menu").css({
			opacity : 0.5
		});
		for ( var i = 0; i < checkboxes.length; i++) {
			if (checkboxes[i].type == 'checkbox') {
				checkboxes[i].checked = false;
			}
		}
	}
}
/**
 * This function is called to check shared status of document.
 **/				
function sharedStatus(documentId,docType,documentName,shareStatus,sharedEntityId){
	sharedStatus(documentId,docType,documentName,shareStatus,'true',sharedEntityId);
}
/**
 * This function is called to check shared status of document.	
 **/	
function sharedStatus(documentId,docType,documentName,shareStatus, isOwnDV,sharedEntityId){
	var infoFlag = true;
	//<!-- CQ 8914 read only role R7.2.0 -->
	var role_observer = document.getElementById("role_current");
	//console.log('---sharedStatus---');
	//console.log('---role_current: '+role_observer.value);
		
	if((null != sharedEntityId && sharedEntityId != '' && documentId != sharedEntityId) || (role_observer.value == 'OBSERVER'))
		{
		infoFlag = false;
		
		}
	$('#lockingForViewhidden').val(infoFlag);
	
		pageGreyOut();
		 $.ajax({
	    	type: "POST",
		    url: $('#linkage').val()+"&documentId="+documentId+"&docType="+docType+"&documentName="+documentName+"&shareStatus="+shareStatus+"&isOwnDV="+isOwnDV,
		    success:function(data)
		    {
		    	removePageGreyOut();
		    	var $response=$(data);
            var data = $response.contents().find(".overlaycontent");
            $("#displayshared").empty();
            if(data != null || data != ''){
            	$("#displayshared").html(data.detach());
			}
			$("#removeprovlabel").html(name);
			$("#overlayedJSPContent").html($response);
			$('#docTypeHidden').val(docType);
			$(".overlay").launchOverlay($("#overlayedJSPContent"), $(".exit-panel, #closeoverlay"), "400px", null, "onReady");
		    }
		});
		 
}
/**
 * This function is called to locking For View.	
 **/
function lockingForView()
{
	var lockingFlag = $('#lockingForViewhidden').val();
	if(lockingFlag == 'false')
		{
		$('[name="provCheck"]').attr('disabled',true);
		// Added fix for defect #7773 
		$('#removeall').hide();
		$('#removeselected').hide();
		// defect #7773 end 
		}
}
/**
 * This function is called when you unshare an entity.		
 **/
function unShareEntity(){
	$('#menuFlag').val("true");
	var shareFlag = true;
	if($('#documentVaultGrid input[name="check"]:checked').size() > 0)
	{
		$("td span.red-ex-mark").remove();
		$('#documentVaultGrid input[name="check"]:checked').each(function() {
			var res = (this.value).split(",");
			if(null != res[2] && res[2] != '' && res[2] != 'null' && res[0] != res[2])
			{
				$(".messagediv")
				.html(
				"At least 1 of the selected documents or folders you selected are part of a Shared folder. " +
				"The exclamation mark(s) below indicate the item(s) that cannot be un-shared. Please de-select these items and try again."
				+ "<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
				$(".messagediv").addClass('failed');
				$(".messagediv").show();
				shareFlag = false;
				$("#documentVaultGrid input[name='check'][value*='"+res[0]+"']").closest("tr").find("td").eq(1).append("<span class='red-ex-mark red-ex-mark-documentVault'>&nbsp;</span>")
				removePageGreyOut();
				//return false;
			}
		});
	}
	var url = $("#contextPathVal").val()+'/GetContent.jsp?lockingFlag=true&nextAction=unShare';
	$('#myform').attr('action', url);
	var optionsForMove = {
		success : function(responseText,statusText, xhr) {
		if(responseText == '' || responseText == 'true')
		{
			$(".messagediv")
			.html(
			"A transaction is currently in progress for the selected documents. Please try again at a later time."
			+ "<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
			$(".messagediv").addClass('failed');
			$(".messagediv").show();
			$(".overlay")
			.closeOverlay();
			removePageGreyOut();
		}
		else
		{
			if(shareFlag)
			{
				pageGreyOut();
				unshareAllDocument(provFormAction);
				var options = {
						success : function(responseText, statusText, xhr) {
							var $response = $(responseText);
							var data = $response.contents().find(".overlaycontent");
							$("#unshareall").empty();
							if (data != null && data != '') 
							{
								$("#unshareall").html(data.detach());
							}
							$("#overlayedJSPContent").html($response);
							$(".overlay").launchOverlay($(".alert-box-unshareall"),
							$(".exit-panel"),"430px", "auto", "onReady");
							removePageGreyOut();
						},
						error : function(xhr, ajaxOptions, thrownError) {
							showErrorMessagePopup();
							removePageGreyOut();
						}
					};
					$(document.provform).ajaxSubmit(options);
					return false;
				}
			}
		},
		error : function(xhr, ajaxOptions,thrownError) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	};
	$(document.provform).ajaxSubmit(optionsForMove);
}

/**
 * This function is called when you delete a document forever.		
 **/
function deleteForever(docId, entityName) {
	var checkedItem = "";
	$("#documentVaultGrid input[type='checkbox']:checked").each(function(){
		if($(this).val() != "on")
		{
		checkedItem += $(this).val().split(",")[0]+','+$(this).val().split(",")[1] +"~";
		}
	});
	if(docId == '')
	{
		$('#menuFlag').val("true");
	}
	else
	{
		$('#menuFlag').val("false");
	}
	$('#checkedDocumentId').val(docId);
	$('#checkedDocumentType').val(entityName);
			pageGreyOut();
			var jqxhr = $.ajax(
					{
						url : $("#contextPathSession").val()+"/GetContent.jsp?action=checkDocExits&documentId="+docId+"&documentType="+entityName+"&jspName="+$('#presentFolderId').val(),
						type : 'POST',
						data: {checkedItem :  checkedItem },
						success : function(data) 
						{
							if(data == 'FilenotFound')
							{	
								removePageGreyOut();
								var _message= "The selected file/folder has been deleted.";
								//openRootNodeInTree('leftTree');
								var folderId = $js("#leftTree").jstree("get_selected");
								openFolder(folderId, "", "leftTree", null, null, null, null, null);
								$(".messagediv").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
						  		$(".messagediv").addClass("failed");
						  		$(".messagediv").show();
							}
							else
							{
								$('#deletedId').val(docId);
								$('#docTypeForRecycle').val(entityName);
								$(".overlay").launchOverlay($(".alert-box-delete-forever"),$(".exit-panel"), "400px", "auto", null);
								removePageGreyOut();
							}	
						},
						error : function(data, textStatus, errorThrown) {
						},
						complete : function() {
						}
					});
			
}
/**
 * This function is called when you delete an entity.		
 **/
function deleteEntity() {
	pageGreyOut();
	document.provform.action = provFormAction + "&submit_action=deleteForever";

	var options = {
		success : function(responseText, statusText, xhr) {
			var response = new String(responseText);
			var responses = response.split("|");
			if (!(responses[1] == "Error" || responses[1] == "Exception")) {
				$(".overlay").closeOverlay();
				var data = $(responseText);
				$("#myform .tabularWrapper").replaceWith(
						data.find(".tabularWrapper"));
				var _message = data.find("#message").val();
				var _messageType = data.find("#messageType").val();

				$(".messagediv")
						.html(
								_message
										+ "<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
				$(".messagediv").removeClass("failed passed");
				$(".messagediv").addClass(_messageType);
				$(".messagediv").show();
				$(".overlay").closeOverlay();
				removePageGreyOut();
			}
			// Added condition to hide/show EmptyBin
			if ($("#hideEmptyRecycleBin").size() > 0
					&& parseInt($("#hideEmptyRecycleBin").val()) == 0) {
				$("#emptyBin").hide();
			} else if ($("#hideEmptyRecycleBin").size() > 0
					&& parseInt($("#hideEmptyRecycleBin").val()) > 0) {
				$("#emptyBin").show();
			}
			// End
		},
		error : function(xhr, ajaxOptions, thrownError) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	};
	$(document.provform).ajaxSubmit(options);

}
/**
 * This function is called for empty RecycleBin Overlay.
 **/
function emptyRecycleBinOverlay(){
	var url = $("#contextPathVal").val()+'/GetContent.jsp?lockingFlag=true&checkedDocumentId=RecycleBin&nextAction=emptyRecycleBin';
	pageGreyOut();
	$(".overlay").launchOverlay($(".alert-box-Empty-RecycleBin"), $(".exit-panel"),
			"400px", "auto", null);
	removePageGreyOut();
}
/**
 * This function is called to open Shared Folder.
 **/
function openSharedFolder(providerId, provName)
{
	pageGreyOut();
	$('#sharedPageOrg').val(providerId);
	document.myOrgform.action = orgFormAction+'&submit_action=manageOrganization&providerId='+providerId+"&provName="+provName;
	  var options1 = {
				success : function(responseText, statusText, xhr) {
					var response = new String(responseText);
					var responses = response.split("|");
					if(!(responses[1] == "Error" || responses[1] == "Exception"))
					{
				        var data = $(responseText);
				        $(".tabularWrapper").replaceWith(data.find(".tabularWrapper"));
				        removePageGreyOut();
					}
				},
				error : function(xhr, ajaxOptions, thrownError) {
					showErrorMessagePopup();
					removePageGreyOut();
				}
			};
	$(document.myOrgform).ajaxSubmit(options1);
}

/**
 * This function is called when empty Recycle Bin is clicked.
 **/
function emptyRecycleBin(){
	pageGreyOut();
	document.provform.action = provFormAction+"&submit_action=emptyRecycleBin";

	var options = {
			success : function(responseText, statusText, xhr) {
				var response = new String(responseText);
				var responses = response.split("|");
				if(!(responses[1] == "Error" || responses[1] == "Exception"))
				{
					$(".overlay").closeOverlay();
				
			        var data = $(responseText);
			       
			        	$("#myform .tabularWrapper").replaceWith(data.find(".tabularWrapper"));
			 	        var _message = data.find("#message").val();
			 	        var _messageType = data.find("#messageType").val();
			 	        $(".messagediv").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
			 	        $(".messagediv").removeClass("failed passed");
			 	        $(".messagediv").addClass(_messageType);
			 			$(".messagediv").show();
			 			$(".overlay").closeOverlay();
			 			//added for hiding Empty Bin
			 	        $("#emptyBin").hide();
			 	        //End
			 	        removePageGreyOut();
			 	        
			   		  
				}
			},
			error : function(xhr, ajaxOptions, thrownError) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		};
$(document.provform).ajaxSubmit(options);
}
				
// End
/**
 * This function is called to refresh Left Tree.
 **/
function refreshLeftTree(preselectId){
	$js('#leftTree').jstree("destroy");
	if(preselectId==null || typeof preselectId == 'undefined')
		preselectId = "DocumentVault";
	tree($('#hdnopenTreeAjaxVar').val(), 'leftTree', 'selectedfolderid', preselectId,'','');	
}
/**
 * This function is called when any field in searching is updated.
 * The function is updated in Release 6.
 * The key and value for Returned Payment is added in Agency options.
 **/
function searchFieldsUpdate(){
	var selectedOrganizationType = "";
	var mainTreeSelected = $js("#leftTree").jstree("get_selected");
	var selectOrgnizationTreeSelected = $js("#selectOrgnization").jstree("get_selected");
	if(selectOrgnizationTreeSelected.length > 0 && $js("#selectOrgnization").jstree(true)){
		selectedOrganizationType = $js("#selectOrgnization").jstree(true).get_node(selectOrgnizationTreeSelected).data.organizationType;
		if(loggedInUserOrgType != 'city_org'){
			$("#findshareddocform #dropdownul2").html($("#docType"+selectedOrganizationType).html());
			$("#doctype_city").typeHeadDropDown("refresh");
		}
		else
		{
			$("#searchform #dropdownul1").html($("#docType"+selectedOrganizationType).html());
			$("#filtertype").typeHeadDropDown("refresh");
			//Updated in release 6
			var agencyOptions = [{key:"", value:"" }, {key:"amendment", value:"Amendment"},{key:"agencyAward", value:"Agency Award"},{key:"Contract", value:"Contract"},{key:"providerAward", value:"Provider Award"},{key:"returnedPayment", value:"Returned Payment"}];
			//Updated in Release 6 end
			var providerOptions = [{key:"", value:""},
			                       {key:"Award", value:"Award"},
			                       {key:"Budget", value:"Budget"},
			                       {key:"Business Application", value:"Business Application"},
			                       {key:"Filings", value:"Filings"},
			                       {key:"Invoice", value:"Invoice"},
			                       {key:"Proposal", value:"Proposal"},
			                       {key:"Service Application", value:"Service Application"}];
			var optionsToPopulate = providerOptions;
			if(selectedOrganizationType=='agency_org'){
				optionsToPopulate = agencyOptions;
			}
			var linkedValue = $("#linked").val(), optionSelected=""; 
			$("#linked").html("");
			$.each(optionsToPopulate, function(key, value) {
				optionSelected = "";
				if(linkedValue == value.key)
					optionSelected = "selected";
	            $("#linked").append("<option value='"+value.key+"' "+optionSelected+">" + value.value + "</option>");
	        });
		}
	}else if(mainTreeSelected.length > 0){
		var optionShow;
		var populateOrganizationType=loggedInUserOrgType;
		if($("#manageOrganization").length > 0)
		{
			populateOrganizationType = $js("#leftTree").jstree(true).get_node(mainTreeSelected).data.organizationType;
		}
		if(loggedInUserOrgType == 'city_org'){
			$("#searchform #dropdownul1").html($("#docType"+populateOrganizationType).html());
			$("#filtertype").typeHeadDropDown("refresh");
			
			optionShow =   [{key:"", value:""},
			                       {key:"Procurement", value:"Procurement"}];
			
			
			var linkedValue = $("#linked").val(), optionSelected=""; 
			if($("#manageOrganization").length > 0)
				{
						if(populateOrganizationType == 'agency_org')
							{
							//Updated in release 6
							optionShow = [{key:"", value:"" }, {key:"amendment", value:"Amendment"},{key:"agencyAward", value:"Agency Award"},{key:"Contract", value:"Contract"},{key:"providerAward", value:"Provider Award"},{key:"returnedPayment", value:"Returned Payment"}];
							//Updated in release 6 end
							}
						else if(populateOrganizationType == 'provider_org')
							{
							optionShow = [{key:"", value:""},
							                       {key:"Award", value:"Award"},
							                       {key:"Budget", value:"Budget"},
							                       {key:"Business Application", value:"Business Application"},
							                       {key:"Filings", value:"Filings"},
							                       {key:"Invoice", value:"Invoice"},
							                       {key:"Proposal", value:"Proposal"},
							                       {key:"Service Application", value:"Service Application"}];
							}
				}
			$("#linked").html("");
			
			$.each(optionShow, function(key, value) {
				optionSelected = "";
				if(linkedValue == value.key)
					optionSelected = "selected";
	            $("#linked").append("<option value='"+value.key+"' "+optionSelected+">" + value.value + "</option>");
	        });	
		}else if($("#manageOrganization").length > 0){
			$("#findshareddocform #dropdownul2").html($("#docType"+populateOrganizationType).html());
			$("#doctype_city").typeHeadDropDown("refresh");
		}
	}
}
/**
 * This function is called to find Shared Document.
 **/
function findSharedDocumentSubmit(form){
	var jspName;
	var optionShared = {
			success : function(responseText, statusText, xhr)
			{
				var response = new String(responseText);
				var responses = response.split("|");
				if (!(responses[1] == "Error" || responses[1] == "Exception")) {
					removePageGreyOut();
					var data = $(responseText);
					if (data.find(".tabularWrapper").size() > 0) {
						$(".tabularWrapper").html(data.find(".tabularWrapper").html());
						$(".tabularWrapper").addClass("tabularWrapperDocumentVaultGreen");
						removePageGreyOut();
					}
				} else {
					removePageGreyOut();
				}
			}
	};
	if($("#findOrgDoc").hasClass("selected")){
		jspName = 'sharedSearchAgency';
		$("#sharedSearchOrgType").val("");
		$("#sharedSearchOrgId").val("");
	}else{
		jspName = 'provdocumentlist';
		var treeId;
		if($js("#leftTree").jstree("get_selected").length > 0)
		{
			treeId = "leftTree";
		}
		else
		{
			treeId = "selectOrgnization";
		}
		
		if( $("#manageOrganization").length > 0){
			treeId = "leftTree";
		}
		var selectOrgnizationTreeSelected = $js("#"+treeId).jstree("get_selected");
		$("#sharedSearchOrgType").val($js("#"+treeId).jstree(true).get_node(selectOrgnizationTreeSelected).data.organizationType);
		$("#sharedSearchOrgId").val($js("#"+treeId).jstree(true).get_node(selectOrgnizationTreeSelected).data.organizationId);
	}
	document.findshareddocform.action = findshareddocform+"&submit_action=filterdocuments&isFilter=true&sharedFlag=true&jspName="+jspName+"&action=documentVault";
	$(document.findshareddocform).ajaxSubmit(optionShared);
	pageGreyOut();
	$("#findshareddocform #findDoc").hide();
}
/**
 * This function is called when submit button is clicked on search.
 **/
function searchFormSubmit(form){
	var folderId;
	var searchFlag;
	$("#clickedSearch").val("clickedSearch");
	if($('#selectedTree').val() == 'selectOrgnization')
	{
		
		folderId = $js("#selectOrgnization").jstree("get_selected");
		searchFlag = true;
	}
	else
	{
		folderId = $js("#leftTree").jstree("get_selected");
		searchFlag = false;
	}
	var mainTreeSelected = $js("#leftTree").jstree("get_selected");
	var selectOrgnizationTreeSelected = $js("#selectOrgnization").jstree("get_selected");
	if(mainTreeSelected.length > 0){
		$("#normalSearchOrgType").val($js("#leftTree").jstree(true).get_node(mainTreeSelected).data.organizationType);
		$("#normalSearchOrgId").val($js("#leftTree").jstree(true).get_node(mainTreeSelected).data.organizationId);
	 }else if(selectOrgnizationTreeSelected.length > 0 && $js("#selectOrgnization").jstree(true)){
		$("#normalSearchOrgType").val($js("#selectOrgnization").jstree(true).get_node(selectOrgnizationTreeSelected).data.organizationType);
		$("#normalSearchOrgId").val($js("#selectOrgnization").jstree(true).get_node(selectOrgnizationTreeSelected).data.organizationId);
	 }else if($("#findOrgDoc").hasClass("selected")){
		$("#normalSearchOrgType").val("");
		$("#normalSearchOrgId").val("");
	 }
	document.searchform.action = searchFormAction+"&searchFlag="+searchFlag+"&folderId="+folderId+"&submit_action=filterdocuments&isFilter=true&action=documentVault&sharedFlag=false";
	var option1 = {
			success : function(responseText, statusText, xhr)
			{
				var response = new String(responseText);
				var responses = response.split("|");
				if (!(responses[1] == "Error" || responses[1] == "Exception")) {
					removePageGreyOut();
					var data = $(responseText);
					if (data.find(".tabularWrapper").size() > 0) {
						$("#myform .tabularWrapper").html(
								data.find(".tabularWrapper").html());
						if($("#hideEmptyRecycleBin").size() > 0 && parseInt($("#hideEmptyRecycleBin").val()) == 0){
							$("#downloadAll").hide();
						}else if($("#hideEmptyRecycleBin").size() > 0 && parseInt($("#hideEmptyRecycleBin").val()) > 0){
							$("#downloadAll").show();
						} 
						$(".overlay").closeOverlay();
						removePageGreyOut();
					}
				} else {
					$(".overlay").closeOverlay();
					var _message= "You must select at least 1 search criteria.";
					$(".messagediv").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
			  		$(".messagediv").addClass("failed");
			  		$(".messagediv").show();
					removePageGreyOut();
				}
			}
		};
	$(document.searchform).ajaxSubmit(option1);
	pageGreyOut();
	$("#searchDoc").hide();
}
/**
 * This function is called to open Tree Node when selected.
 **/
function selectTreeNodeForOpenFolder(folderId)
{
	var treeFlag = $js('#leftTree').jstree(true).get_node(folderId);
	if(treeFlag != false)
	{
		$js("#leftTree").jstree("deselect_all");
		$js('#leftTree').jstree("select_node", folderId);
	}
	else
	{
		$js("#selectOrgnization").jstree("deselect_all");
		$js('#selectOrgnization').jstree("select_node", folderId);
	}
}
/**
 * This function is called to run parallel processing in bulk operations.		
 **/
function parallelProcessMethod(action, callBackOnSuccess, time){
	document.provform.action = provFormAction + '&submit_action=checkParallelProcessProgress&action='+action;
	var options = {
		success : function(responseText, statusText, xhr) {
			var responseString = new String(responseText);
			var responsesArr = responseString.split("|");
			if(responsesArr[1] != "in progress" && responsesArr[1] != "null"){
				if (!(responsesArr[1] == "Error" || responsesArr[1] == "Exception")) {
					var data = $(responseText);
					if (data.find(".tabularWrapper").size() > 0) {
						$("#documentVaultGrid.tabularWrapper").replaceWith(data.find(".tabularWrapper"));
						var _message = data.find("#message").val();
						var _messageType = data.find("#messageType").val();
						$(".messagediv")
								.html(
										_message
												+ "<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
						$(".messagediv").removeClass("failed passed");
						$(".messagediv").addClass(_messageType);
						$(".messagediv").show();
						$(".overlay").closeOverlay();
						if(action == 'move')
						{
							refreshLeftTree(data.find("#selectedfolderidformove").val());
						}
						else
						{
							refreshLeftTree($js('#leftTree').jstree("get_selected"));
						}
					}else {
						$(".messagediv")
								.html(
										"Your application has generated an error."
										+"<a href='#' title='Contact Admin'>Contact Admin</a>"
												+ "<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
						$(".messagediv").addClass('failed');
						$(".messagediv").show();
						$(".overlay")
								.closeOverlay();
						removePageGreyOut();
					}
				}else {
					$(".messagediv")
							.html(
									responsesArr[3]
											+ "<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
					$(".messagediv").addClass(responsesArr[4]);
					$(".messagediv").show();
					$(".overlay")
							.closeOverlay();
				}
				removePageGreyOut();
			}else{
				setTimeout(function() { parallelProcessMethod(action, callBackOnSuccess, time); }, time);
			}
				if(callBackOnSuccess !=null && typeof callBackOnSuccess != "undefined"){
					callBackInWindow(callBackOnSuccess);
				}
		},
		error : function(xhr, ajaxOptions,
				thrownError) {
			showErrorMessagePopup();
			removePageGreyOut();
		}
	};
	$(document.provform).ajaxSubmit(options);
}
/**
 * This function is called to run parallel processing in bulk operations.	
 **/
function ajaxSubmitParallelProcessingMain(url, action, time, callBackOnSuccess){
	pageGreyOut();
	document.provform.action = url;
	var options = {
			success : function(responseText, statusText, xhr) {
				var responseString = new String(responseText);
				var responsesArr = responseString.split("|");
				if (!(responsesArr[1] == "Error" || responsesArr[1] == "Exception")) {
					var data = $(responseText);
					if (data.find(".tabularWrapper").size() > 0 && data.find("#messageType").val() != '') {
						var _message = data.find("#message").val();
						var _messageType = data.find("#messageType").val();
						$(".messagediv")
								.html(
										_message
												+ "<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
						$(".messagediv").removeClass("failed passed");
						$(".messagediv").addClass(_messageType);
						$(".messagediv").show();
						$(".overlay").closeOverlay();
						removePageGreyOut();
					}else{
						setTimeout(function() { parallelProcessMethod(action, callBackOnSuccess, time); }, time);
					}
				} else {
					$(".messagediv")
							.html(
									responsesArr[3]
											+ "<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
					$(".messagediv").addClass(responsesArr[4]);
					$(".messagediv").show();
					$(".overlay").closeOverlay();
					removePageGreyOut();
				}
			},
			error : function(xhr, ajaxOptions,
					thrownError) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		};
		$(document.provform).ajaxSubmit(options);
}
/**
 * This function is used to Add condition to hide/show EmptyBin	
 **/
function restoreCallBack(){
	if($("#hideEmptyRecycleBin").size() > 0 && parseInt($("#hideEmptyRecycleBin").val()) == 0){
		$("#emptyBin").hide();
	}else if($("#hideEmptyRecycleBin").size() > 0 && parseInt($("#hideEmptyRecycleBin").val()) > 0){
		$("#emptyBin").show();
	}
	//End
}