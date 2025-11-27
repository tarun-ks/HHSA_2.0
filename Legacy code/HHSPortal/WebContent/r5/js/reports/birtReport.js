/**
 * This Js file has functions for birt report
 **/
var suggestionVal = "";
/**
 * This function is executed to view detailed report	
 **/
function viewDetailedReport(reportId){
	if(reportId!=null && reportId!="undefined" && reportId!="")
	{
			$("#birtReportForm").attr(
					"action",
					$("#birtReportForm").attr("action")
							+ "&next_action=filterReports&reportId="+reportId+"&requestReportType="+$("#requestReportType").val());
			document.birtReportForm.submit();
	}
	else
		{
		$("#birtReportForm").attr(
				"action",
				$("#birtReportForm").attr("action")
						+ "&next_action=filterReports&requestReportType="+$("#requestReportType").val()+"&isCompititionPoolEnabled="+$("#compitionPool").prop('disabled'));
		document.birtReportForm.submit();
		}
	}

/**
 * This function is executed when there is jump to detailed report
 **/
function jumpToDetailedReport(){
	if($("#reportId").val() =='')
		{
		$("#requestReportType").val('financials');
		}
	
		$("#birtReportForm").attr(
				"action",
				$("#birtReportForm").attr("action")
						+ "&next_action=jumpToReports&requestReportType="+$("#requestReportType").val());
		document.birtReportForm.submit();
	}
/**
 * This function is executed to get table info
 **/
function getTabInfo(tabName){
	if(tabName == 'Invoice')
		{
		$("#Contract").removeClass("selected");
		$("#Invoice").addClass("selected");
		}
	else
		{
		$("#Invoice").removeClass("selected");
		$("#Contract").addClass("selected");
		}
	$("#tabName").val(tabName);
	paging();
}

/** 
 * This will execute when Previous,Next.. is clicked for pagination
 **/
function paging(pageNumber) {
			pageGreyOut();
			var v_parameter = "nextPage=" + pageNumber + "&reportId=" + $("#reportId").val()+"&tabName=" + $("#tabName").val();
			var urlAppender = $("#dataGridReportPaging").val();
			jQuery.ajax({
				type : "POST",
				url : urlAppender,
				data : v_parameter,
				success : function(e) {
					$("#reportListDataGrid").html(e);
					removePageGreyOut();
				},
				error : function(data, textStatus, errorThrown) {
					removePageGreyOut();
				}
			});
}

		

/**
 * This funtion fetches program name list depending upon agency selected by user
 **/
function getProgramNameList() {
	var url = $("#getProgramListForAgency").val() + "&agencyId="
			+ $("#agency").val();
	var jqxhr = $.ajax({
		url : url,
		type : 'POST',
		cache : false,
		success : function(data) {
			removePageGreyOut();
			
			if($("#agency").prop("selectedIndex")!=0)
				{
				$("#optionsBox").html(data);
				$("#typeheadbox").prop('disabled', false);
				}
		},
		error : function(data, textStatus, errorThrown) {
		}
	});
}

/**
 * This method set the visibility of pop up up whether it should be enable or disable.
 **/
function setVisibility(id, visibility) {
	callBackInWindow("closePopUp");
	if ($("#" + id).is(":visible")) {
		settoDefaultFilters();
	}
	$("#" + id).toggle();
}

$(document).ready(function() {
//for program name 
	       var inputBox = $("#typeheadbox");
			//gets the  program Name if the agency is valid
			$("#agency").change(function() {
				agency = $(this).prop("selectedIndex");
				$('#typeheadbox').val("");
				if (agency == 0) {
					$("#optionsBox").val("");
					
					$("#optionsBox").prop('disabled', true);
					$('#dropdownul').hide();
				} else {
					getProgramNameList();
				}
			});
			inputBox.unbind("keyup").unbind("click").unbind("keydown").keyup(function(e){
				var key = e.keyCode;
				if(key ==  13)
				{
					e.stopPropagation();
					return false;
				}
				var dataToSearch = $(this).val().toLowerCase();
				if(dataToSearch.length>2)
				{
					$("#optionsBox").show();
					$("#optionsBox .data").each(function(){
						if($(this).html().toLowerCase().indexOf(dataToSearch)>-1){
							$(this).show();
						}else{
							$(this).hide();
						}
					});
				}
				else
				{
					$("#optionsBox .data").each(function(){
							$(this).show();
					});
					$("#optionsBox").hide();
				}
			if($("#optionsBox .data:visible").size()==0)
			{
				$("#optionsBox").hide();
			}
			}).click(function(e){
				e.stopPropagation();
			}).keydown(function(e){
				$('#login').attr('disabled', 'disabled');
				var $listItems = $("#dropdownul li:visible");
				var key = e.keyCode,
		        $selected = $listItems.filter('.selectLiCombo'),
		        $current;
				if(key ==  13) // Enter key
				{
					e.stopPropagation();
					return false;
				}
			    if ( key != 40 && key != 38 ) return;

			    $listItems.removeClass('selectLiCombo');

			    if ( key == 40 ) // Down key
			    {
			        if ( ! $selected.length || $selected.is(':last-child') ) {
			        	if(!$selected.is(':last-child'))
			            	$current = $listItems.eq(0);
			        	else
			        		$current = $selected;
			        }
			        else {
			            $current = $selected.next();
			        }
			    }
			    else if ( key == 38 ) // Up key
			    {
			        if ( ! $selected.length || $selected.is(':first-child') ) {
			        	if(!$selected.is(':first-child'))
			            	$current = $listItems.last();
			        	else
			        		$current = $selected;
			        }
			        else {
			            $current = $selected.prev();
			        }
			    }
			    $current.addClass('selectLiCombo');
			    $parentDiv = $('#dropdownul');
			    if(!isScrolledIntoView($parentDiv, $current))
			    	$parentDiv.scrollTop($current.position().top);
			});
		$('#optionsBox').on('mouseenter',function(e) {
				$('#optionsBox ul li').on('click',function(e) {
					$("#organizationIdKey").val($(this).attr('key'));
					inputBox.val($(this).text());
					$("#optionsBox").hide();
					e.stopPropagation();
			});
				$('#optionsBox ul li').on('mouseover',function(e) {
					$(this).addClass("selectLiCombo");
					e.stopPropagation();
		        });
		        $('#optionsBox ul li').on('mouseout',function (e) {
		        	$(this).removeClass("selectLiCombo");
		        	e.stopPropagation();
		        });
			});
				 
		   
			//start'
			if ($("#typeheadbox").length) {
				var offset = inputBox.offset();
				var leftToSubtract = 0;
				$("#optionsBox").css({
					top: offset.top + inputBox.offsetHeight + 3
				});
				
				$("#combotable_button").click(function(e){
					agency = $('#agency').prop("selectedIndex");
					if (agency == 0) {
						$('#dropdownul').hide();
					}
					$("#optionsBox").toggle();
					e.stopPropagation();
				});
				$(document).click(function(){
					$("#optionsBox").hide();
				});
		}
			
			
			
			
	//for export
		$( "#reportExportButton" ).click(function() {
			window.open($("#contextPathSession").val()+"/GetContent.jsp?actionParam=exportDetailReport&reportId="+$("#reportId").val()+"&tabName="+$("#tabName").val());
		});

			
	var jspName = $("#jspName").val();
	if(jspName!=null && jspName!="" && jspName!="undefined")
		{
	$(".reportButton").hide();	
		}
	
	var reportType = $("#requestReportType").val();
	if(reportType!=null && reportType!="" && reportType!="undefined" && reportType == 'procurement')
		{
		$('#section_reportfinancials').removeClass('current');
		$('#section_reportprocurement').addClass('current');
		}
	else
		{
		$('#section_reportfinancials').addClass('current');
		$('#section_reportprocurement').removeClass('current');
		}
	
	
	$("#programName").prop('disabled', true);
	$(".tableContractValue").each(function(e) {
		$(this).autoNumeric('init', {vMax: '9999999999999999',vMin:'-9999999999999999.99'});
	});
	
	if ($("#agency").prop("selectedIndex") == 0) {
	$("#typeheadbox").prop('disabled', true);
	}
	
	//gets the  program Name if the agency is valid
	$("#agency").change(function() {
		agency = $(this).prop("selectedIndex");
		if (agency == 0) {
			$("#typeheadbox").val("");
			$("#typeheadbox").prop('disabled', true);
		} else {
			pageGreyOut();
			getProgramNameList();
		}
	});
	
	if($("#orgType").val() =='agency_org')
	{
	getProgramNameList();
	}
	if ($("#agency").prop("selectedIndex") != 0) {
		getProgramNameList();
	}
	//typehead for contract number
	typeHeadSearch($('#ctId'), $(
			"#getContractNoListResourceUrl").val()
			+ "&contractNoQueryId=fetchContractNoList",null,"typeHeadCallBackCtId",null);
	
	//typehead for contract Title
	typeHeadSearch($('#contractTitle'), $(
			"#getContractNoListResourceUrl").val()
			+ "&contractNoQueryId=fetchContractTitleList",null,"typeHeadCallBackContractTitle",null);

	//typehead for provider name
	typeHeadSearch($('#provider'), $(
	"#hiddengetProviderListResourceUrl").val(),
	null, "typeHeadCallBackProvider", null);
	
	//typehead for procurement Title
	typeHeadSearch($('#procurementTitle'), 
			$("#hiddenGetProcurementListResourceUrl").val()+ "&QueryId=fetchProcurementTitleList",
			$("#compitionPool").attr('id'),
			null,
			null,
			"procurementId",
			"getCompetitionData"
			);
	
	// Added for 7432
	$("#compitionPool").prop('disabled', true);
	
	if ($("#procurementTitle").length) {
		$("#compitionPool").prop('disabled', false);
		getCompetitionData();
	}
		});
// Added for defect 7422
/**
 * this function is added to get competition pool data
 * */
function getCompetitionData() {
	typeHeadSearch($('#compitionPool'), $(
			"#hiddenGetCompetitionListResourceUrl").val()
			+ "&QueryId=fetchCompetitionPoolList&procurementId="
			+ $("#procurementId").val(), null, null, null, null, null);
}

/**
 * Set the filter to default values
 * */
function settoDefaultFilters() {
	$('.defaultFilterTextBox').val('');
	$('.defaultFilter').find('option:first').attr('selected', 'selected');
	$("#compitionPool").val('');
	$("#compitionPool").prop('disabled', true);
	$("span.error").empty();
	var d = new Date();
	var year = d.getFullYear();
	var mon = d.getMonth();
	if(mon>6)
		{
		year = year+1;
		}
	$("#fyYearId").val(year);
	$("#typeheadbox").prop('disabled', true);
	var orgType = $("#orgType").val();
	if (orgType == "agency_org") {
		$("#agency").val($("#Agency_ID").val());
		getProgramNameList();
	}
}

/**
 * This method calls typeHeadCallBackCtId passing ctId as parameter
 * */
function typeHeadCallBackCtId() {
	commonTypeHeadCallBack($('#ctId').val());
	}
/**
 * This method calls typeHeadCallBackCtId passing contractTitle as parameter
 * */
function typeHeadCallBackContractTitle() {
	commonTypeHeadCallBack($('#contractTitle').val());
	}
/**
 * This method calls typeHeadCallBackCtId passing ctId as parameter
 * */
function typeHeadCallBackProvider() {
	commonTypeHeadCallBack($('#provider').val());
}
