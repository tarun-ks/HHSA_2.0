//This method called when page is getting loaded and set the values
$(document).ready(
		function() {
			// enable disable add cp button based on title
			$("#competitionPoolTitle").keyup(
					function() {
						var titleValue = $(this).val();
						$("span.error").text("");
						if (titleValue.length >= 1) {
							$("#addCPButton").attr("disabled", false);
						} else {
							$("#addCPButton").attr("disabled", true);
						}
					});
			// on click of add button
			$("#addCPButton").click(
					function() {
						var data = $("#competitionPoolTitle").val();
						var isExist = $("#selectedCompetitionPools option")
						.filter(function() {
							return this.value === data;
						}).length !== 0;
						if(isExist){
							$("#competitionPoolTitle").parent().next().text("! Competition Pool already exists");
						}
						else if($("#competitionPoolTitle").val().length < 3){
							$("#competitionPoolTitle").parent().next().text("! You must enter 3 or more characters");
						}
						else{
							$('#selectedCompetitionPools').append(
									$("<option></option>").attr("value", data).attr("title", data)
											.text(data));
							$("#competitionPoolTitle").val("");
							$("#addCPButton").attr("disabled", true);
						}
					});

			// on select deselect from multi select box
			$('#selectedCompetitionPools').bind(
					"click keyup keydown blur",
					function() {
						if ($("#selectedCompetitionPools option:selected")
								.size() > 0) {
							$("#removeCPButton").attr("disabled", false);
						} else {
							$("#removeCPButton").attr("disabled", true);
						}
					});

			// on click of remove button
			$("#removeCPButton").click(
					function() {
						$("#selectedCompetitionPools option:selected").remove();
						$("#removeCPButton").attr("disabled", true);
					});
			// form submission validation
			$("#save").click(function(){
					pageGreyOut();
					$("#selectedCompetitionPools option").attr('selected', true);
					document.competitionPoolForm.submit();
			});
		});