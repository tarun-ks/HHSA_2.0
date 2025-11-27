
//to be called when the page will be loaded
$(document)
		.ready(
				function() {
					//on click of the hyperlink of report link
					$("#openLink").click(function() {
						window.open($("#link").val());
					});
					//on click of the email address.
					$("#email").click(function() {
						window.location.href = "mailto:"+$("#mail").val();
					});
					//to change the format of tyhe currency
					$("#procValueSpan").changeCurrency();
					//on click of the next button
					$("#next")
							.click(
									function() {
										document.procurementSummaryform.action = document.procurementSummaryform.action
												+ "&nextAction=saveNext";
										document.procurementSummaryform.submit();
									});
					var procDescVal = $("#hiddenProcDesc").val();
					procDescVal = procDescVal.replace(/\n/g, "<br/>");
					$("#procDesc").html(procDescVal);

				});

/**
 * This function is used to set the page grey out
 */
function setPageGreyOut(){
	pageGreyOut();
}
