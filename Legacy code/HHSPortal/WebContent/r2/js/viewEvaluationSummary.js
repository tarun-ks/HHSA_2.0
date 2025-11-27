/**
 * ========================================
 * This file handles the event related to
 * Evaluation summary screen.
 * ========================================
 */
//ready function
var dataArray = new Array();
// On document Load
	$(document).ready(function(){
		$("#evaluatorScoreList").find("tr").each(function(i){
			if(i!=0){
				if(i%2 == 0){
					$(this).addClass("oddRows");
				}else{
					$(this).addClass("evenRows");
				}
			}
		});
		$(".wlp-bighorn-header").hide();
		$(".footer").hide();
		
		var increment = 1;
		var values = "";
		var finalValues1 = "";
		var finalValues2 = "";
		var j = 0;
		$("tr[id^=avgCount]").each(function(j){
			$(this).find("td").each(function(i){
				//change for fixing average issue
				if(i!=0){
					if($(this).find("span").html()!=null){
					dataArray[j] = dataArray[j]+","+$(this).find("span").html().trim();
					}else{
						dataArray[j] = dataArray[j]+",0";
					}
				}else{
					dataArray[j] = "";
				}
			});
		});
		var totalLength = dataArray.length;
		for(var i = 0;i<dataArray.length;i++){
			var data = dataArray[i].split(",");
			for(var j = 1;j<data.length;j++){
				var currentValue = $("#avgScore"+j).html();
				if(j==data.length-1){
					currentValue = $("#avgScoreFinal").html();
				}
				var valueToSet="";
				if($.trim(currentValue).length > 0)
					valueToSet = parseFloat(currentValue) + parseFloat(data[j]/totalLength);
				else
					valueToSet = (data[j]/totalLength);
				if(isNaN(valueToSet)){
					valueToSet = "";
				}
				if(j==data.length-1){
					$("#avgScoreFinal").html(valueToSet);
				}else{
					$("#avgScore"+j).html(valueToSet);
				}
			}
		}
		$("td[id^=avgScore]").each(function(i){
				var amt = $(this).html();
				amt = parseFloat(amt).toFixed(2);
				if(isNaN(amt)){
					amt = "";
				}
				$(this).html(amt);
		});
		
		//Added in R5 on Checkbox change
		$('input[type="checkbox"]').change(function() {
			var _totVersion = $('#totalVersion').val();
			if($(this).is(':checked')){
				onChecked(_totVersion);
			}
			else{
				onUnChecked(_totVersion);
			}
		});
	});

/**
 * 	 Added in R5: to show/hide Evaluation Criteria Table
 **/
	function showScoreCriteria(){
		$('#evalCriteriaDiv').toggle();
		if($("#container1").is(':visible'))
		{
			$("#container1").hide();
			$("#container2").show();
		}
		else
		{
			$("#container2").hide();
			$("#container1").show();
		}
	}
	
	/**
	 * Added in R5: for dropdown change round information
	 **/
	 function onChangeRound(obj){
		pageGreyOutForSummary();
		var _totVersion = $('#totalVersion').val();
		for(var i=1 ; i<=_totVersion; i++)
		{
			if(i == obj.value)
			{
				$('#totalRoundAccordian'+obj.value).show();
				for(var j=1; j<=$('#totEval').val(); j++){
					if($('#accordionHeaderId'+obj.value+'_'+j).html() != null){
						if($('#accordionTopId'+obj.value+'_'+j+' div').attr('style').indexOf('arrowCollapse') > 0)
							{
								$('#accordionHeaderId'+obj.value+'_'+j).trigger('click');
							}
					}
				}
				if($('#accordionTopIdAccoComments'+i+' div').html() != null && $('#accordionTopIdAccoComments'+i+' div').attr('style').indexOf('arrowCollapse') > 0){
					$('#accordionHeaderIdAccoComments'+i).trigger('click');	
				}
			}
			else
			{
				$('#totalRoundAccordian'+i).hide();
			}
		}
		removePageGreyOutForSummary();
	}
	 
	/**
	 * Added in R5: for show/hide evaluation score information
	 **/
	function showEvalInfo(obj){
		var evalId = obj.charAt(obj.length - 1);
		$('.eval'+evalId).toggle();
		if($('#'+obj).find('.container3').css('display') != "none"){
			$('#'+obj).find('div.container3').hide();
			$('#'+obj).find('div.container4').show();
		}else{
			$('#'+obj).find('div.container3').show();
			$('#'+obj).find('div.container4').hide();
		}
	}
	
	/**
	 *  Added in R5: This Function call on Check the CheckBox
	 **/
	function onChecked(_totVersion){
		$('#roundOption').hide();
		for(var i=1; i<=_totVersion; i++){
			$('#totalRoundAccordian'+i).hide();
			$('.eval'+i).hide();
			$('#round'+i).hide();
		}
		if(_totVersion == 1){
			for(var i=1; i<=$('#totEval').val(); i++){
				$('#accordionTopIdAccoComment'+i).show();
				if($('#accordionHeaderIdAccoComment'+i).html() != null)
				{
					if($('#accordionHeaderIdAccoComment'+i).attr('style').indexOf('arrowExpand') > 0)
					{
						$('#accordionHeaderIdAccoComment'+i).trigger('click');
					}
				}
			}
		}
		$('#totalRoundAccordian'+(++_totVersion)).show();
		$('.eval'+_totVersion).show();
		$('#round'+_totVersion+' td').show();
		
		if($('#accordionHeaderIdAccoComments'+_totVersion).html() != null && $('#accordionHeaderIdAccoComments'+_totVersion).attr('style').indexOf('arrowExpand') > 0 ){
			$('#accordionHeaderIdAccoComments'+_totVersion).trigger('click');
		}
		
		for(var i=1; i<=$('#totEval').val(); i++)
		{
			if($('#accordionTopId'+_totVersion+'_'+i).html() != null && $('#accordionTopId'+_totVersion+'_'+i+' div').attr('style').indexOf('arrowExpand') > 0){
				$('#accordionTopId'+_totVersion+'_'+i+' div').trigger('click');
			}
		}
		$('#round'+_totVersion).removeClass();
	}
	
	/**
	 *  Added in R5: This Function call on unCheck the CheckBox
	 **/
	function onUnChecked(_totVersion){
		for(var i=1; i<=_totVersion; i++){
			$('#round'+i).show();
			$('.eval'+i).hide();
			$('#round'+i).find('div.container3').show();
			$('#round'+i).find('div.container4').hide();
		}
		$('#roundOption select').val(_totVersion);
		$('#roundOption select').trigger('onchange');
		$('#roundOption').show();
		$('#totalRoundAccordian'+(++_totVersion)).hide();
		$('.eval'+_totVersion).hide();
		$('#round'+_totVersion+' td').hide();
	}