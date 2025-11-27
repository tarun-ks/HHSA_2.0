var data = null;
//On page load
$(document).ready(
		function() {
			$("#filter").click(function(){
				documentFilter();
			});
			$("#clearfilter").click(function(){
				reset();
			});
		});
//sets the visibility filter pane		
function setVisibility(id, visibility) {
	$("#" + id).toggle();
	callBackInWindow("closePopUp");
}
//resets the filter
function reset(){
	$("#documentCategoryFilter option").eq(0).attr("selected", "selected");
}
//This method is invoked when user click on any page link to navigate between the pages
function paging(pageNumber) {
	var url = $("#helpform").attr("action");
	$("#nextAction").val("helpdocuments");
	$("#nextPageParam").val(pageNumber);
	document.helpform.submit();
}
//This function will be called for filtering the documents
function documentFilter() {
	$("#helpform").attr("action",$("#helpform").attr("action"));
	$("#nextAction").val("helpdocuments");
	document.helpform.submit();
}
//resets the filter
function reset(){
	document.getElementById("documentCategoryFilter").value = "";
}