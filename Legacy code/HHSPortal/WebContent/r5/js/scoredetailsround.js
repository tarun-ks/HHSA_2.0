/**
 * This js file is used to get scored details
 * This function is executed when there is change in round
 * value score will be updated 
 **/
function onChangeRound(obj, evalStatusId){
		pageGreyOut();
		var _round = obj.value;
		if(isEvalRoundEmpty(_round)){
			var v_parameter = "&versionNumber=" + _round + "&evaluationStatusId=" + evalStatusId;
			var urlAppender = $("#getEvaluatorRoundDetailsUrl").val();
			jQuery.ajax({
				type : "POST",
				url : urlAppender,
				data : v_parameter,
				success : function(result) {
					hideNotRequiredRound(_round);
					$('#evaluationRoundDetail_'+_round).html(result);
					totalScoreUpdate(_round,evalStatusId);
					removePageGreyOut();
				},
				error : function(result) {
					showErrorMessagePopup();
					removePageGreyOut();
				}
			});
		}else{
			hideNotRequiredRound(_round);
			removePageGreyOut();
		}
	}
/**
 * This function is executed when total score is updated
 **/	
	function totalScoreUpdate(_round, evalStatusId){
		var _totalScore = $('#'+_round+'_evalScoreGenTable_'+evalStatusId+' tbody tr td:eq(1) label').html().replace('Total Score: ','');
		if($('#'+_round+'_evalScoreGenTable_'+evalStatusId+' tbody tr').find('td:eq(1)').hasClass('darkGray isModifiedDarkGray'))
			$('#totalScore_'+_round).addClass('isModifiedDarkGray');
		$('#totalScore_'+_round).html('Total Score: '+_totalScore);
	}
/**
 * This function is executed when evaluation round is empty
 **/		
	function isEvalRoundEmpty(round){
		var isEmpty = true;
		if($('#evaluationRoundDetail_'+round).html() != ""){
			isEmpty = false;
		}
		return isEmpty;
	}