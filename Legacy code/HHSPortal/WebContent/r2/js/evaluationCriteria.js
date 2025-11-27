/**
 * This method called when page is getting loaded and set the values and initialize the events
 */
$(document)
		.ready(
				function() {
					var totalScoreFlag = true;
					$('input[id^="scoreFlag"]').change(function(){
						var scoreId = $(this).attr("id");
						var scoreSeq = scoreId.charAt(9);
						$("#questionFlagError" + scoreSeq + "").html("");
						$("#scoreFlagError" + scoreSeq + "").html("");
					});
					for ( var i = 0; i <= 9; i++) {
						if (!($("#scoreFlag" + i + "").attr('checked'))) {
							var scoreCriteriaObj = $("#scoreCriteria" + i);
							var maxScoreObj = $("#maximumScore" + i);
							$(scoreCriteriaObj).attr("readonly", "readonly").addClass("readonly");
							$("#scoreCriteria" + i + "").val('');
							$(maxScoreObj).attr("readonly", "readonly").addClass("readonly");
							$("#maximumScore" + i + "").val('');
						}
					}
					var procurementStatus = $("#procurementStatus").val();
					if (!(procurementStatus == '2' || procurementStatus == '3')) {
						$(".proposalRecStatus").prop('disabled', true);
					}
					$("#hidden").hide();
					$("#save")
							.click(
									function() {
										$(".totalScoreMsg").hide();
										validationFlag = validateQuestionText();
										checkScoreFlag = checkScore();
//										if(validationFlag && checkScoreFlag){
//											totalScoreFlag = findTotalScore();
//										}
										if (validationFlag && checkScoreFlag
												&& findTotalScore()) {
											document.evaluationCriteriaform.action = document.evaluationCriteriaform.action
													+ "&nextAction=save";
											pageGreyOut();
											document.evaluationCriteriaform
													.submit();
										}
									});
				});
/**
 * This function is used to enable the text boxes
 * @param obj object
 * @param counter number
 */
function enableTextBox(obj, counter) {
	var scoreCriteriaObj = $("#scoreCriteria" + counter);
	var maxScoreObj = $("#maximumScore" + counter);
	if ($(obj).attr("checked")) {
		$(scoreCriteriaObj).removeClass("readonly");
		$(scoreCriteriaObj).attr('readonly', false).addClass("readonlyDisabled");
		$(scoreCriteriaObj).prop('readonly', false);
		$(maxScoreObj).removeClass("readonly");
		$(maxScoreObj).attr('readonly', false).addClass("readonlyDisabled");
		$(maxScoreObj).prop('readonly', false);
		$(obj).attr("value", "1");
	} else {
		$(scoreCriteriaObj).attr("readonly", "readonly").addClass("readonly").removeClass("readonlyDisabled");
		$("#scoreCriteria" + counter + "").val('');
		$(maxScoreObj).attr("readonly", "readonly").addClass("readonly").removeClass("readonlyDisabled");;
		$("#maximumScore" + counter + "").val('');
		$("#questionFlagError" + counter + "").html("");
		$(obj).attr("value", "0");
	}
}
/**
 * This function validate the question text field against the text box
 * @returns {Boolean} return true false
 */
function validateQuestionText() {
	var tmpIsError = true;
	for ( var i = 0; i <= 9; i++) {
		
		if ($("#scoreFlag" + i + "").attr('checked')) {
			if ($("#scoreCriteria" + i + "").val().trim() == "") {
				$("#questionFlagError" + i + "").html(
						"! This field is required.");

				tmpIsError = false;
			} else {
				$("#questionFlagError" + i + "").html("");

			}
			if ($("#maximumScore" + i + "").val().trim() == "") {
				$("#scoreFlagError" + i + "").html("! This field is required.");
				tmpIsError = false;
			} else {
				$("#scoreFlagError" + i + "").html("");

			}
		} else {
			$("#questionFlagError" + i + "").html("");
			$("#scoreFlagError" + i + "").html("");
		}
	}
	return tmpIsError;
}

/**
 * This function calculate the total score for the evaluation
 * @returns {Boolean} return true false
 */
function findTotalScore() {
	var maxScoreTotal = 0;
	for (i = 0; i <= 9; i++) {
		if ($("#scoreFlag" + i + "").attr('checked')) {
			var currentMaxScore = $("#maximumScore" + i).val();
			if ((!isNaN(parseInt(currentMaxScore)))) {
				maxScoreTotal = parseInt(maxScoreTotal)
						+ parseInt(currentMaxScore);
				/*if(nullCheck){
					nullCheck = false;
				}*/
			}
		}
	}
	if (maxScoreTotal != 100) {
		$(".totalScoreMsg").show();
		return false;
	} else {
		$(".totalScoreMsg").hide();
		return true;
	}
	return true;
}

/**
 * This function check the score of the evaluation box
 * @returns {Boolean} return true false
 */
function checkScore() {
	var statusFlag = true;
	for ( var i = 0; i <= 9; i++) {
		if ($("#scoreFlag" + i + "").attr('checked')) {
			var id = "maximumScore" + i;
			var value = $("#" + id + "").val();
			if ((parseInt(value)) > 100 || (parseInt(value)) < 1) {
				var errorId = "scoreFlagError" + i;
				$("#" + errorId + "").html("! Score must be between 1 and 100");
				statusFlag = false;
			} else {
				var errorId = "scoreFlagError" + i;
				if($("#" + errorId + "").html() == "! Score must be between 1 and 100")
					$("#" + errorId + "").html("");
			}
		}
	}
	return statusFlag;
}
