// This method will apply css to table
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
$(".alert-box-address").find('#canceladdrvalidation').click(function() {
	$(".overlay").closeOverlay();
	return false;
});