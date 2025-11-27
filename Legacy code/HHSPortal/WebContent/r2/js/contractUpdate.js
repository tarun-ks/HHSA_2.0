//called On click of save
	function onSaveClick() {
		$("#errorGlobalMsg").html("");
		$("#errorGlobalMsg").hide();
		$("#successGlobalMsg").html("");
		$("#successGlobalMsg").hide();
		if(validateComment()){
		pageGreyOut();
		var v_parameter = "&budgetID=" + budgetID + "&contractID=" + contractID + "&publicCommentArea=" + convertSpecialChar($("#publicCommentArea").val());
		var urlAppender = $("#saveContractBudgetUpdateUrl").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(result) {
				if(result.indexOf("contractUpdateFYBudget")!=-1){
		        	$("#assignAdvanceId").html(result);
		        	resetFlag();
		        }else {
		        	$("#errorGlobalMsg").html(result);
					$("#errorGlobalMsg").show();
		        }
		    	removePageGreyOut();
			},
			error : function(result) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		});
		}else{
			$("#errorGlobalMsg").html(invalidResponseMsg);
			$("#errorGlobalMsg").show();
		}
	}
	// function to refresh non grid data for contracted services
	function refreshNonGridUpdateContractedServicesData(subBudgetIdVal){
		var v_parameter = '&nextAction=getContractedServicesData&subBudgetId='+subBudgetIdVal;
		var urlAppender = $("#getCallBackData").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(e) {
				$("#totCS"+e['SubBudgetId']).html(e['ProposedTotalContractedServicesAmount']).jqGridCurrency();
				$("#ytdIA"+e['SubBudgetId']).html(e['TotalYtdInvoiceAmount']).jqGridCurrency();
				return false;
			},
			error : function(data, textStatus, errorThrown) {
				removePageGreyOut();
			}
		});
	}
	
	// This function closes the overlay
	function clearAndCloseOverLay() {
		$("#overlayDivId").html("");
		$(".overlay").closeOverlay();
	}

	//This function opens the submit confirmation overlay
	function openOverlay() {
		$("#errorGlobalMsg").html("");
		$("#errorGlobalMsg").hide();
		$("#successGlobalMsg").html("");
		$("#successGlobalMsg").hide();
		pageGreyOut();
		var jspName = "submitCBUpdateConfirmation";
			//Release 3.6.0 Enhancement id 6484
		var v_parameter = "&jspName=" + jspName + "&budgetID=" + budgetID + "&fiscalYearID=" +fiscalYearID
				+ "&contractID=" + contractID + "&ctId=" + ctId + "&agencyID=" + "${contractInfo.agencyId}";
		var urlAppender = $("#submitContractBudgetUpdateOverlayUrl").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(result) {
				if (result["error"] == 1)// For validation failure
				{
					$("#errorGlobalMsg").show();
					$("#errorGlobalMsg").html(result["message"]);
				} 
				else if(result["error"] == 2) // Application exception
				{
					$("#errorGlobalMsg").show();
					$("#errorGlobalMsg").html(result["message"]);
				}else{
					pageGreyOut();
					$("#overlayDivId").html(result);
					$(".overlay").launchOverlayNoClose(
							$(".alert-box-submit-contract"), "850px", null,
							"onReady");
					$("a.exit-panel").click(function() {
						clearAndCloseOverLay();
					});
				}
				removePageGreyOut();
			}
		});
	}
	


	// function to refresh non grid data for personnel services update
	function refreshNonGridData(subBudgetIdVal){
		var v_parameter = '&nextAction=getPersonnelServicesData&subBudgetId='+subBudgetIdVal;
		var urlAppender = $("#getCallBackData").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(e) {
				$("#val1"+e['SubBudgetId']).html(e['TotalSalaryAndFringeAmount']).jqGridCurrency();
				$("#val2"+e['SubBudgetId']).html(e['TotalSalaryAmount']).jqGridCurrency();
				$("#val3"+e['SubBudgetId']).html(e['TotalFringeAmount']).jqGridCurrency();
				if(e['FringePercentage'] == 0){
					$("#val5"+e['SubBudgetId']).html('(0.00%)');
				}else{
					if(e['FringePercentage'].indexOf('E-') !== -1 || e['FringePercentage'].indexOf('e-') !== -1){
						$("#val5" + e['SubBudgetId']).html('('+ new Big(Math.round(e['FringePercentage'].replaceAll('e-', 0).replaceAll('E-', 0) * 100) / 100).toFixed(2)+ '%)');
					}else{
						$("#val5" + e['SubBudgetId']).html("(" +new Big(Math.round(e['FringePercentage'] * 100) / 100).toFixed(2)+ "%)");
					}
				}
				$("#val4"+e['SubBudgetId']).html(e['TotalYtdInvoicedAmount']).jqGridCurrency();
				if($('#existingBudget').val() == 0){
					var cellValue = $('#val5'+subBudgetIdVal).html();
					if(cellValue != null){
						cellValue = cellValue.replace('(','').replace(')','');
						var fringeObj = $('#table_fringeBenifitsGrid-'+subBudgetIdVal+'_>tbody>tr:eq(1)>td:eq(3)');
						fringeObj.removeClass();
						fringeObj.css("text-align", "center");
						fringeObj.html(cellValue);
						fringeObj.attr('title',cellValue);
					}
				}
				return false;
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		});
	}	

	// This Overide the JQGrid's Edit Row
	var oldEditRow = $.fn.jqGrid.editRow;
	$.jgrid.extend({
		editRow: function (iRow,iCol, ed){
			var totalColumn='';
				if(iRow.substring(iRow.lastIndexOf('_') + 1) == 'newrow'){
					totalColumn = getSubGridIds(iRow);
					var tempStringlen = totalColumn.split(',');
					var len = tempStringlen.length;
					for ( var i = 0; i < len; i++) {
						$(this).jqGrid('setColProp',tempStringlen[i], {editable : true});
						if (notEditableForAddRow != null) {
							var tmpnotEditableForAddRow = notEditableForAddRow.split(',');
							for ( var count = 0; count < tmpnotEditableForAddRow.length; count++) {
								if (tempStringlen[i] == tmpnotEditableForAddRow[count]) {
									$(this).jqGrid('setColProp',tempStringlen[i],{editable : false});
								}
							}
						}
					}
	   		}
				
				

   
		if (iRow.indexOf('_newrecord_contractServices') !== -1 && isGridEditFn == 'false')
		{
			// start:Added in R7 for 8972
			if($('#'+$(this).attr('id') +'_ilsave').length>0)
				{
				// End:Added in R7 for 8972
				totalColumn = getSubGridIds(iRow);
				var tempStringlen = totalColumn.split(',');
				$(this).jqGrid('setColProp', tempStringlen[0], {editable : true});
				$(this).jqGrid('setColProp', tempStringlen[1], {editable : true});
			}
        }
		else if (iRow.indexOf('_newrecord_rent') !== -1 && isGridEditFn == 'false')
        {
			totalColumn = getSubGridIds(iRow);
			var tempStringlen = totalColumn.split(',');
			for(var count=0; count<5; count++){
				$(this).jqGrid('setColProp',tempStringlen[count], {editable : true});
			}
			$('#' + iRow + '>td').each(function(i) {
				if(i==4){
					if ($(this).html()[$(this).html().length - 1] === '%'){
	                    $(this).html($(this).html().replace('%',''));
					}
				}
	            
			});
		}
        else if (iRow.indexOf('_newrecord') !== -1 && isGridEditFn == 'false')
        {
			totalColumn = getSubGridIds(iRow);
			var tempStringlen = totalColumn.split(',');
			$(this).jqGrid('setColProp',tempStringlen[0], {editable : true});
		}
		else if (iRow.indexOf('new_row') !== -1)
		{
			totalColumn = getSubGridIds(iRow);
			var tempStringlen = totalColumn.split(',');
			$(this).jqGrid('setColProp',tempStringlen[0], {editable : true});
		}
		else if(isGridEditFn == 'true')
		{
			totalColumn = getSubGridIds(iRow);
			var tempStringlen = totalColumn.split(',');
			for(var count=0; count<tempStringlen.length; count++ )
			{
				$(this).jqGrid('setColProp',tempStringlen[0], {editable : false});
			}
		}
		else
		{
			totalColumn = getSubGridIds(iRow);
			var tempStringlen = totalColumn.split(',');
			$(this).jqGrid('setColProp',tempStringlen[0], {editable : false});
		}
	       return oldEditRow.call (this, iRow, iCol, ed); 
	   }
	});
	
	// This function is to get total grid'd Id
	function getSubGridIds(rowid){ 
		var tempString='';
		$('#'+rowid+'>td').each(function(i){
			var finalId = getColName(this);
			if(tempString != ''){
				tempString = tempString + ','+ finalId;
			}else{
				tempString = finalId;
			}
		});
	return tempString;
	}
	
	// This function will return the column's bean name
	function getColName(obj){
			var tempId = $(obj).attr('aria-describedby');
			var n = tempId.lastIndexOf('_');
		return tempId.substring(n + 1);	
	}
	
	//function to refresh non grid data for OTPS screen, contract budget modification module
	function refreshNonGridDataContBudModOTPS(subBudgetIdVal){
		var v_parameter = '&nextAction=getOperationSupportModData&subBudgetId='+subBudgetIdVal;
		var urlAppender = $("#getCallBackData").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(e) {
				$("#fyBudgetModOTPS"+subBudgetIdVal).html(e['keyFYBudgetModOTPS']).jqGridCurrency();
				$("#ytdInvAmtModOTPS"+subBudgetIdVal).html(e['keyYTDInvAmtModOTPS']).jqGridCurrency();
				return false;
			},
			error : function(data, textStatus, errorThrown) {
				removePageGreyOut();
			}
		});
	}
	
	// This function is used trim the string
	function trim(stringToTrim) {
		return stringToTrim.replace(/^\s+|\s+$/g,"");
	}	
		// This method is called for client side finish task validation
	function finishTaskValidation(){
			var returnVal = true;
			var publicCommentVal = "";
			var internalCommentVal = "";
			if(document.getElementById("internalCommentArea")!=null){
				internalCommentVal=trim(document.getElementById("internalCommentArea").value);
			}if(document.getElementById("publicCommentArea")!=null){
				publicCommentVal=trim(document.getElementById("publicCommentArea").value);
			}
			var taskStatus = $("#finishtaskchild").val();
			if(taskLevel==1 && publicCommentVal=="" && taskStatus=="Returned for Revision"){
				$("#taskErrorDiv").html(publicCommentErrorMsg);
				$("#taskErrorDiv").show();
				returnVal = false;
			}else if(taskLevel>1 && internalCommentVal=="" && taskStatus=="Returned for Revision"){
				$("#taskErrorDiv").html(internalCommentErrorMsg);
				$("#taskErrorDiv").show();
				returnVal = false;
			}
			return returnVal;
		}
	// This method refreshes the indirect rate data
	function refreshNonGridIndirectRateData(subBudgetIdVal){
		var v_parameter = '&nextAction=getIndirectRateData&subBudgetId='+subBudgetIdVal;
		var urlAppender = $("#getCallBackData").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(e) {
				//Updated in R7
				$("#indirectRate"+subBudgetIdVal).text(e.keyIndirectRatePercent);
				$("#indirectPIRate"+subBudgetIdVal).text(e.keyPIIndirectRatePercent);
				//R7 End
				return false;
			},
			error : function(data, textStatus, errorThrown) {
				removePageGreyOut();
			}
		});
	}
	
	//Start : Added in R6
	/**
	* This function called to refresh non-grid data on PS Enhancement Screen
	* Added in Release 6
	**/
	function refreshPSSummaryNonGridData(subBudgetIdVal, PSScreen){
		
		var v_parameter = '&nextAction=getPersonnelServicesSummaryData&subBudgetId='+subBudgetIdVal;
		var urlAppender = $("#getCallBackContractBudgetData").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(e) {
				if(PSScreen == 'PSSummaryScreen'){
					$("#val1"+e['SubBudgetId']).html(e['CitySalaryAndFringeAmount']).jqGridCurrency();
					$("#val2"+e['SubBudgetId']).html(e['CitySalaryAmount']).jqGridCurrency();
					$("#val3"+e['SubBudgetId']).html(e['CityFringeAmount']).jqGridCurrency();
					$("#val4"+e['SubBudgetId']).html(" ("+e['FringePercentage']+"%)");
					$("#val5"+e['SubBudgetId']).html(e['TotalYtdInvoicedAmount']).jqGridCurrency();
					$("#val6"+e['SubBudgetId']).html(e['Position']);
				}
				else{
					$("#val8"+e['SubBudgetId']).html(e['DetailedScreenMessage']);
					$("#val1"+e['SubBudgetId']).html(e['CitySalaryAndFringeAmount']).jqGridCurrency();
					$("#val2"+e['SubBudgetId']).html(e['CitySalaryAmount']).jqGridCurrency();
					$("#val3"+e['SubBudgetId']).html(e['CityFringeAmount']).jqGridCurrency();
					$("#val4"+e['SubBudgetId']).html(" ("+e['FringePercentage']+"%)");
					$("#val6"+e['SubBudgetId']).html(e['Position']);
					$("#val7"+e['SubBudgetId']).html(e['totalCityFte']);	
				}
				return false;
			},
			error : function(data, textStatus, errorThrown) {
				removePageGreyOut();
			}
		});
	}
	
	/**
	* This function called to refresh the FringeGrid header(Rate Column)
	* Added in Release 6
	**/
	function refreshFringGridHeader(subBudgetIdVal){
		var cellValue = $('#val5'+subBudgetIdVal).html();
		if(cellValue != null)
		{
			cellValue = cellValue.replace('(','').replace(')','');
			var fringeObj = $('#table_fringeBenifitsGrid-'+subBudgetIdVal+'_>tbody>tr:eq(1)>td:eq(3)');
			fringeObj.removeClass();
			fringeObj.css("text-align", "center");
			fringeObj.html(cellValue);
			fringeObj.attr('title',cellValue);
		}
	}
	
	/**
	* This function called On loadcomplete of Hourly grid to calculate the percentage.
	* Added in Release 6
	**/
	function refreshHourlyPositionHeader(subBudgetIdVal, jsonObj){
		var tableIdObj = '#table_hourlyPositionDetailsGrid-';
		var tbodyObj = '>tbody>tr:eq(1)>td:eq(';
		var fyBudgetHeaderObj = $(tableIdObj+subBudgetIdVal+tbodyObj+'5)');
		var cityFundedHeaderObj = $(tableIdObj+subBudgetIdVal+tbodyObj+'6)');
		var totalRateNHour = null;
		for(var i=0; i<jsonObj.rows.length; i++){
			if(totalRateNHour == null){
				totalRateNHour = jsonObj.rows[i].rate * jsonObj.rows[i].hourPerYear;
			}
			else{
				totalRateNHour = totalRateNHour + jsonObj.rows[i].rate * jsonObj.rows[i].hourPerYear;
			}
		}
		var cityFundVal = ((fyBudgetHeaderObj.html().replace('$','')/totalRateNHour)*100).toFixed(2) + '%';
		if(cityFundVal.indexOf('NaN%') === 0){
			cityFundVal = "0.00%";
		}
		cityFundedHeaderObj.html(cityFundVal);
		cityFundedHeaderObj.attr('title',cityFundVal);
	}
	//End : Added in R6