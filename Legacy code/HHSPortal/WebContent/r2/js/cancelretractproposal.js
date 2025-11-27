// on page load
$(document).ready(function() {

	$("#proposalAction").change(
						function() {
							if ($(this).val() == 'Cancel Proposal') {
								pageGreyOut();
								$(".overlay").launchOverlayNoClose(
										$(".alert-box-cancelProposal"),
										"450px", null);
								removePageGreyOut();
								return false;
							}

							if ($(this).val() == 'Retract Proposal') {
								pageGreyOut();
								$(".overlay").launchOverlayNoClose(
										$(".alert-box-retractProposal"),
										"450px", null);
								removePageGreyOut();
								return false;
							}

						});

				$("#doNotCancelProposal").click(function(e) {
					overlayLaunched.closeOverlay(e);
				});

				$(".cancelProposal-exit").click(function(e) {
					if (overlayLaunched != null) {
						e.stopPropagation();
						overlayLaunched.closeOverlay(e);
						resetEpinOverLayVals();
					}
				});
				$("#yesCancelProposal").click(
						function(e) {
							$("#proposalSummaryForm").attr("action",
									$("#cancelURL").val())
							$("#proposalSummaryForm").submit();
						});

				//retract proposal

				$("#doNotRetractProposal").click(function(e) {
					overlayLaunched.closeOverlay(e);
				});

				$(".retractProposal-exit").click(function(e) {
					if (overlayLaunched != null) {
						e.stopPropagation();
						overlayLaunched.closeOverlay(e);
						resetEpinOverLayVals();
					}
				});
				$("#yesRetractProposal").click(
						function(e) {
							$("#proposalSummaryForm").attr("action",
									$("#retractURL").val())
							$("#proposalSummaryForm").submit();
						});

				//retract proposal end

			});