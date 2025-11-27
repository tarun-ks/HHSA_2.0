/**
 * 
 */
var uploadfileForm = document.uploadform.action;
//on load function to perform various checks on loading of jsp
function onReady(){
	// This will execute when upload button is clicked
	$('#next1').click(function() {
		pageGreyOut();
	    var url = $("#resourceURL").val()+"&next_action=fileinformation";
	    var jqxhr = $
		.ajax({
			url : url,
			type : 'POST',
			cache : false,
			success : function(response) {
			   	$("#tab1").empty();
				$("#tab2").empty();
				if(response != null || response != ''){
			    	$("#tab2").html(response);
				}
				$(".overlay").launchOverlay($(".alert-box-upload"), $(".exit-panel.upload-exit"), "850px", null, "onReady");
				removePageGreyOut();
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		});
	});
		// This will Execute when any option is selected for filter Document
		// Category
		$('#docCategory').change(function() {
			pageGreyOut();
			$('#docType').html('');
			getDocumentType();
			removePageGreyOut();
		});
		// this method will be called when any user click on upload button on
		// upload screen
		$('#uploadfile').change(function()
				{
					pageGreyOut();
					displayDocName(this);
					removePageGreyOut();
				});
}

//will return the List of document types through servlet call
function getDocumentType() {
	var docCategory = $("#docCategory").val();
	var url = $("#uploadDocUrlHidden").val()
			+ "&next_action=getDocumentType&docCategory=" + docCategory;
	var jqxhr = $
			.ajax({
				url : url,
				type : 'POST',
				cache : false,
				success : function(data) {
						var dataJSON = $.parseJSON(data);
						$.each(dataJSON["documentTypeList"], function(i, val) {
						    $('#docType').append('<option value="' + val.id + '">' + val.value + '</option>');
						});
				      },
				error : function(data, textStatus, errorThrown) {
				}
			});
	
	// This will execute when Cancel button is clicked during file upload
	$(".alert-box-upload").find('#cancel').unbind("click").click(function() {
		$(".overlay").closeOverlay();
		return false;
	});
}

// This will execute when any option is selected from Document Category drop down 
// and will hide - unhide various div depending upon category selected
function selectCategory(form, userOrg){
	var e = document.getElementById('doccategory');
	var category = e.options[e.selectedIndex].value;
	if(category == null || category == ""){ 
		document.getElementById("doctype").value=""; 
		document.getElementById("doctype").disabled = true; 
		$('#sampleCategoryDiv').hide();
		$('#sampleTypeDiv').hide();
		return false; 
	}
	if('<%=ApplicationConstants.CITY_ORG%>' == userOrg){
		if("Sample Document" == category){
			getSampleCategory();
			$('#sampleCategoryDiv').show();
			$('#sampleTypeDiv').show();
		}
		else{
			$('#sampleCategoryDiv').hide();
			$('#sampleTypeDiv').hide();
		}
	 return false;
	}
	else{
		getDocumentTypeList(category, userOrg);
		document.getElementById("doctype").disabled = false;
	} 
}
	// This will execute when any file is selected to upload
function displayDocName(filePath) {
		var fullPath = filePath.value;
		var fileNameIndex = fullPath.lastIndexOf("\\") + 1;
		var filename = fullPath.substr(fileNameIndex);
		var docName = document.getElementById("hidden");
		var ext = filename.lastIndexOf(".");
		filename = filename.substr(0, ext);
		$(".alert-box").find("#docName").val(filename);
		$(".alert-box").find("#hidden").show();
		$(".alert-box").find(".docnameError").hide();
	}
