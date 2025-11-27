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
	var url = $("#helpformagency").attr("action");
	$("#nextAction").val("helpdocumentsagency");
	$("#nextPageParam").val(pageNumber);
	document.helpformagency.submit();
}
//This function will be called for filtering the documents
function documentFilter() {
	$("#helpformagency").attr("action",$("#helpformagency").attr("action"));
	$("#nextAction").val("helpdocumentsagency");
	document.helpformagency.submit();
}
//resets the filter
function reset(){
	document.getElementById("documentCategoryFilter").value = "";
}