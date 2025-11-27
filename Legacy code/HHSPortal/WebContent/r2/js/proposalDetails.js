var maxSiteId = -1;
var keySeparator = "k3yv@lu3S3p@r@t0r";
var submitClicked = false;
// executes on page load
$(document).ready(function(){
	$("#providerName").prop('readonly', true);
	$("#providerOfficeTitle").prop('readonly', true);
	$("#providerEmailId").prop('readonly', true);
	$("#providerPhone").prop('readonly', true);
	$(".readOnlyValue").prop('readonly', true);
	$(".readOnlyValue").attr('readonly', 'readonly');
	$(".readOnlyValue").attr('disabled', 'disabled');
	if($("#noSiteMessage").html().trim().length > 0)
		$("#noSiteMessage").addClass("failedShow");
	
	$('#totalFundingRequest').autoNumeric('init', {vMax: '9999999999999999',vMin:'0.00'});
	var serviceUnitFlag = $("#serviceUnitFlag").val();
	if(null != serviceUnitFlag && serviceUnitFlag == '1'){
		updateCostPerUnit();
		$("#costPerUnit").prop('readonly', true);
		$("#totalNumberOfService").validateNumber();
		if($("#costPerUnit") != null && $("#costPerUnit").size() > 0 && $("#costPerUnit").val().length > 0)
			$("#costPerUnit").changeCurrency();
		$("#totalFundingRequest").change(function(){
			updateCostPerUnit();
		});
		$("#totalNumberOfService").change(function(){
			updateCostPerUnit();
		});
	}
	//on click of next button
	$("#nextButton").click(function(){
		$("#proposalDetailsForm1").attr("action", $("#nextProposalDetails").val());
		$("#proposalDetailsForm1").submit();
	});
	//on click of return to proposal Summary hyperlink
	$("#returnProposalSummaryPage").click(function(){
		document.proposalDetailsForm1.action = $("#proposalSummaryUrl").val();
		document.proposalDetailsForm1.submit();
	});
	if($("#providerContactId option:selected").val() == ''){
		$("#providerName").val("");
		$("#providerOfficeTitle").val("");
		$("#providerEmailId").val("");
		$("#providerPhone").val("");
	}
	// on change of member drop down 
	$("#providerContactId").change(function(){
		if($("#providerContactId option:selected").val() == ''){
			$("#providerName").val("");
			$("#providerOfficeTitle").val("");
			$("#providerEmailId").val("");
			$("#providerPhone").val("");
		} else {
			pageGreyOut();
			var jqxhr = $.ajax({
				url : $("#getMemberDetailsURL").val()+ "&providerContactId=" + $("#providerContactId option:selected").val(),
				type : 'POST',
				cache : false,
				success : function(data) {
					data = $.parseJSON(data);
					if(data.memberData!=null){
						$("#providerName").val(data.memberData[0].USER_NAME);
						$("#providerOfficeTitle").val(data.memberData[0].MEMBER_NAME);
						$("#providerEmailId").val(data.memberData[0].EMAIL);
						$("#providerPhone").val(data.memberData[0].PHONE);
					}
					removePageGreyOut();
				},
				error : function(data, textStatus, errorThrown) {
					showErrorMessagePopup();
					removePageGreyOut();
				}
			});
		}
	});
	
	if($("#siteDetailTable tr:last-child").size() > 0 && $("#siteDetailTable tr:last-child").attr("id") != "noSite"){
		maxSiteId = $("#siteDetailTable tr:last-child").attr("id").replace("trId","");
	}
	siteAction();
	//function add site button click action(opens blank overlay) 
	$("#addSiteButton").click(function(){
		$("#indexOpened").val("new");
		$(".overlay").launchOverlayNoClose($(".alertBoxAddSite"), "540px");
	});
	//closes site overlay
	$(".exit-panel-add-site, #cancelOverlay").click(function(){
		resetOverlay();
		$(".overlay").closeOverlay();
	});
	//shows proposal comments
	$("#showProposalComment").click(function(){
		fillAndShowOverlay();
	});
	
	//this method clears and closes overlay
	function clearAndCloseOverLay() {
		$("#overlayDivId").html("");
		$(".overlay").closeOverlay();
	}
	//This method fills and show overlay
	function fillAndShowOverlay() {
		pageGreyOut();
		var v_parameter = "";
		var urlAppender = $("#showProposalCommentsResourceUrl").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(e) {
				$("#overlayDivId").html(e);
				$(".overlay").launchOverlayNoClose($(".alert-box-proposal-comments"), null, null);
				$("a.exit-panel").click(function() {
					clearAndCloseOverLay();
				});
				removePageGreyOut();
			},
			error : function(data, textStatus, errorThrown) {
				removePageGreyOut();
			}
		});
	}

	// client side validation on base(proposal details)
	$("#saveButton, #saveNextButton").click(function(e){
		$("#saveType").val($(this).attr("id"));
		if(!$("#noSite").is(":visible")){
			$("#noSiteMessage").removeClass("failedShow").html("");
			if(!$("#proposalDetailsForm1").valid())
				return false;
		}else{
			$("#noSiteMessage").addClass("failedShow").html("You must enter at least one site address where services will be provided.");
			return false;
		}
	});
	/*This method validates Proposal Detail Form 	Updated Method in R4*/
	/*Add 5 more questions for R7.7.0 QC9180*/
	$("#proposalDetailsForm1").validate({
		rules: {
			"questionAnswerBeanList[0].answerText": {required: {depends: function(element) {return checkIfElementAvailable(element);}}},
			"questionAnswerBeanList[1].answerText": {required: {depends: function(element) {return checkIfElementAvailable(element);}}},
			"questionAnswerBeanList[2].answerText": {required: {depends: function(element) {return checkIfElementAvailable(element);}}},
			"questionAnswerBeanList[3].answerText": {required: {depends: function(element) {return checkIfElementAvailable(element);}}},
			"questionAnswerBeanList[4].answerText": {required: {depends: function(element) {return checkIfElementAvailable(element);}}},
			"questionAnswerBeanList[5].answerText": {required: {depends: function(element) {return checkIfElementAvailable(element);}}},
			"questionAnswerBeanList[6].answerText": {required: {depends: function(element) {return checkIfElementAvailable(element);}}},
			"questionAnswerBeanList[7].answerText": {required: {depends: function(element) {return checkIfElementAvailable(element);}}},
			"questionAnswerBeanList[8].answerText": {required: {depends: function(element) {return checkIfElementAvailable(element);}}},
			"questionAnswerBeanList[9].answerText": {required: {depends: function(element) {return checkIfElementAvailable(element);}}},
			"questionAnswerBeanList[10].answerText": {required: {depends: function(element) {return checkIfElementAvailable(element);}}},
			"questionAnswerBeanList[11].answerText": {required: {depends: function(element) {return checkIfElementAvailable(element);}}},
			"questionAnswerBeanList[12].answerText": {required: {depends: function(element) {return checkIfElementAvailable(element);}}},
			"questionAnswerBeanList[13].answerText": {required: {depends: function(element) {return checkIfElementAvailable(element);}}},
			"questionAnswerBeanList[14].answerText": {required: {depends: function(element) {return checkIfElementAvailable(element);}}},
			"questionAnswerBeanList[15].answerText": {required: {depends: function(element) {return checkIfElementAvailable(element);}}},
			"questionAnswerBeanList[16].answerText": {required: {depends: function(element) {return checkIfElementAvailable(element);}}},
			"questionAnswerBeanList[17].answerText": {required: {depends: function(element) {return checkIfElementAvailable(element);}}},
			"questionAnswerBeanList[18].answerText": {required: {depends: function(element) {return checkIfElementAvailable(element);}}},
			"questionAnswerBeanList[19].answerText": {required: {depends: function(element) {return checkIfElementAvailable(element);}}},
			proposalTitle:{required:true,
				minlength:3,
				maxlength:90},
			competitionPool: {noneSelected: {depends: function(element) {return checkIfElementEnable(element);}}},
			providerContactId: {noneSelected:true},
			totalNumberOfService: {required:{depends: function(element) {return checkIfElementAvailable(element);}},
				minStrict : 0,
				maxStrict : 99999,
				isDecimal : true},
			totalFundingRequest: {
				required : true,
				maxStrict : 9999999999999999.99,
				maxlength : 24,
				minStrict : 0.00
				}
			
		},
		messages: {	
		/*Add 5 more questions for R7.7.0 QC9180*/
			"questionAnswerBeanList[0].answerText": {required: "! This field is required"},
			"questionAnswerBeanList[1].answerText": {required: "! This field is required"},
			"questionAnswerBeanList[2].answerText": {required: "! This field is required"},
			"questionAnswerBeanList[3].answerText": {required: "! This field is required"},
			"questionAnswerBeanList[4].answerText": {required: "! This field is required"},
			"questionAnswerBeanList[5].answerText": {required: "! This field is required"},
			"questionAnswerBeanList[6].answerText": {required: "! This field is required"},
			"questionAnswerBeanList[7].answerText": {required: "! This field is required"},
			"questionAnswerBeanList[8].answerText": {required: "! This field is required"},
			"questionAnswerBeanList[9].answerText": {required: "! This field is required"},
			"questionAnswerBeanList[10].answerText": {required: "! This field is required"},
			"questionAnswerBeanList[11].answerText": {required: "! This field is required"},
			"questionAnswerBeanList[12].answerText": {required: "! This field is required"},
			"questionAnswerBeanList[13].answerText": {required: "! This field is required"},
			"questionAnswerBeanList[14].answerText": {required: "! This field is required"},
			"questionAnswerBeanList[15].answerText": {required: "! This field is required"},
			"questionAnswerBeanList[16].answerText": {required: "! This field is required"},
			"questionAnswerBeanList[17].answerText": {required: "! This field is required"},
			"questionAnswerBeanList[18].answerText": {required: "! This field is required"},
			"questionAnswerBeanList[19].answerText": {required: "! This field is required"},
			proposalTitle: {required: "! This field is required",
				minlength: "! The Proposal Title must be 3 or more characters",
				maxlength: "! The Proposal Title must be 90 or less characters"},
			competitionPool: {noneSelected: "! This field is required"},
			providerContactId: {noneSelected: "! This field is required"},
			totalNumberOfService: {required: "! This field is required",
				minStrict: "! This must be a number greater than 0",
				maxStrict:"! Input should be less than or equal to 5 digits",
				isDecimal: "! This field cannot contain decimals"},
			totalFundingRequest: {
				required : "! This field is required",
				maxStrict : "! Please enter a value less than $10,000,000,000,000,000.00",
				maxlength : "! Input should be less than or equal to 18 digits",
				minStrict : "! This must be a number greater than 0"
					}
			
		},
		submitHandler: function(form){
			if(!$("#noSite").is(":visible")){
				pageGreyOut();
				form.submit();
			}else{
				return false;
			}
		},
		errorPlacement: function(error, element) {
	        error.appendTo(element.parent().parent().find("span.error"));
		}
	});
	// client side validation on site overlay
	$("#addEditSiteForm").validate({
		rules: {
			siteNameOverlay:{required: true,
				maxlength:90,
				allowSpecialChar: ["A", " !@\\\#\\\$%^\\\&*()-_+=|\\\\{}\\\[\\\];:\\\"\\\'<>,.?\\\/`~\xA7"]},
			address1Overlay:{required: true,
				maxlength:60,
				allowSpecialChar: ["A", " \\\#\\\"\\\',.-"]},
			address2Overlay:{maxlength:60,
				allowSpecialChar: ["A", " \\\#\\\"\\\',.-"]},
			cityOverlay:{required: true,
				maxlength:40,
				allowSpecialChar: ["A", " \\\"\\\',.-"]},
			stateOverlay:{noneSelected: true},
			zipcodeOverlay:{required: true,
				maxlength:5,
				allowSpecialChar: ["N", ""]}
		},
		messages: {
			siteNameOverlay:{required: "! This field is required",
				maxlength: "! Input should be less then 90 characters",
				allowSpecialChar: "! Please enter valid text"},
			address1Overlay:{required: "! This field is required",
				maxlength: "! Input should be less then 60 characters",
				allowSpecialChar: "! Please enter valid text"},
			address2Overlay:{maxlength: "! Input should be less then 60 characters",
				allowSpecialChar: "! Please enter valid text"},
			cityOverlay:{required: "! This field is required",
				maxlength: "! Input should be less then 40 characters",
				allowSpecialChar: "! Please enter valid text"},
			stateOverlay:{noneSelected: "! This field is required"},
			zipcodeOverlay:{required: "! This field is required",
				maxlength: "! Input should be less then 5 characters",
				allowSpecialChar: "! Only numeric text allowed"}
		},
		submitHandler: function(form){
			pageGreyOut();
			jQuery.ajax({
			      type : "POST",
			      url : $("#contextPathSession").val() + "/AddressValidationServlet.jsp?" +
			      		"address1="+escape($("#address1Overlay").val())+
			      		"&city="+escape($("#cityOverlay").val())+
			      		"&state="+escape($("#stateOverlay").val())+
			      		"&zipcode="+escape($("#zipcodeOverlay").val()),
			      data : "",
			      success : function(e) {
			      	removePageGreyOut();
			      	$("#addressDiv").empty();
	                $("#addressDiv").html(e);
	                if(e.indexOf("byPassValidation")>-1){
						 var selectedRadio = $(".rdoBtn:checked")
							.parent().parent();
						  var valueToSet = returnSpace(selectedRadio.find("input[type='hidden'][name='StatusDescriptionText']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='StatusReason']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='StreetNumberText']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='newAddress']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='newCity']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='newState']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='newZip']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='CongressionalDistrictName']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='Latitude']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='Longitude']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='XCoordinate']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='YCoordinate']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='CommunityDistrict']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='CivilCourtDistrict']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='SchoolDistrictName']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='HealthArea']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='BuildingIdNumber']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='TaxBlock']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='TaxLot']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='SenatorialDistrict']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='AssemblyDistrict']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='CouncilDistrict']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='LowEndStreetNumber']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='HighEndStreetNumber']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='LowEndStreetName']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='HighEndStreetName']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='NYCBorough']").val());
						  $("#addressRelatedData").val(valueToSet);
						  submitForm();
					}else {
						$(".overlay").closeOverlay();
		                 $(".overlay").launchOverlayNoClose($(".alert-box-address"));
		                 $(".alert-box-address").find('#selectaddress').click(function() {
	                       var selectedRadio = $(".rdoBtn:checked").parent().parent();
	                       $("#address1Overlay").val(selectedRadio.find("input[type='hidden'][name='newAddress']").val());
	                       $("#cityOverlay").val(selectedRadio.find("input[type='hidden'][name='newCity']").val());
	                       $("#zipcodeOverlay").val(selectedRadio.find("input[type='hidden'][name='newZip']").val());
	                       $("#stateOverlay>option[value='" + selectedRadio.find("input[type='hidden'][name='newState']").val() + "']").attr('selected', 'selected');
	                       var valueToSet = returnSpace(selectedRadio.find("input[type='hidden'][name='StatusDescriptionText']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='StatusReason']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='StreetNumberText']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='newAddress']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='newCity']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='newState']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='newZip']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='CongressionalDistrictName']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='Latitude']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='Longitude']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='XCoordinate']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='YCoordinate']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='CommunityDistrict']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='CivilCourtDistrict']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='SchoolDistrictName']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='HealthArea']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='BuildingIdNumber']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='TaxBlock']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='TaxLot']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='SenatorialDistrict']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='AssemblyDistrict']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='CouncilDistrict']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='LowEndStreetNumber']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='HighEndStreetNumber']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='LowEndStreetName']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='HighEndStreetName']").val())
			            	+keySeparator+returnSpace(selectedRadio.find("input[type='hidden'][name='NYCBorough']").val());
	                       $("#addressRelatedData").val(valueToSet);
	                       $(".overlay").closeOverlay();
	                       submitForm();
	                       return false;
		                 });
						$("#canceladdrvalidation, .address-exit-panel").unbind("click").click(function() {
							$(".overlay").closeOverlay();
							$(".overlay").launchOverlayNoClose($(".alertBoxAddSite"), "540px");
							return false;
						});
					}
			      }
			});
		}
	});
});

//Submits the overlay form(client end only no server side hit)
function submitForm(){
	setValuesForSite();
	$(".overlay").closeOverlay();
	resetOverlay();
}

//functions sets the value in hidden fields for site values
function setValuesForSite(){
	var siteNameOverlay = $("#siteNameOverlay").val();
	var address1Overlay = $("#address1Overlay").val();
	var address2Overlay = $("#address2Overlay").val();
	var cityOverlay = $("#cityOverlay").val();
	var stateOverlay = $("#stateOverlay>option:selected").val();
	var zipcodeOverlay = $("#zipcodeOverlay").val();
	var addressRelatedData = $("#addressRelatedData").val();
	var indexOpened = $("#indexOpened").val();
	if(indexOpened == "new"){
		maxSiteId++;
		var tableRow = '<tr id="trId'+maxSiteId+'"><td>'
						+siteNameOverlay+'</td><td>'
						+address1Overlay+'</td><td>'
						+address2Overlay+'</td><td>'
						+cityOverlay+'</td><td>'
						+stateOverlay+'</td><td>'
						+zipcodeOverlay+'</td><td>'
						+'<select id="action'+maxSiteId+'" class="siteAction"><option value="0">I need to... </option><option value="1">Edit Site</option><option value="2">Delete Site</option></select>'
						+'<input type="hidden" name="siteDetailsList['+maxSiteId+'].siteName" value="'+siteNameOverlay+'"/>'
						+'<input type="hidden" name="siteDetailsList['+maxSiteId+'].address1" value="'+address1Overlay+'"/>'
						+'<input type="hidden" name="siteDetailsList['+maxSiteId+'].address2" value="'+address2Overlay+'"/>'
						+'<input type="hidden" name="siteDetailsList['+maxSiteId+'].city" value="'+cityOverlay+'"/>'
						+'<input type="hidden" name="siteDetailsList['+maxSiteId+'].state" value="'+stateOverlay+'"/>'
						+'<input type="hidden" name="siteDetailsList['+maxSiteId+'].zipCode" value="'+zipcodeOverlay+'"/>'
						+'<input type="hidden" name="siteDetailsList['+maxSiteId+'].actionTaken" value="insert"/>'
						+'<input type="hidden" name="siteDetailsList['+maxSiteId+'].proposalSiteId"/>'
						+'<input type="hidden" name="siteDetailsList['+maxSiteId+'].addressRelatedData" value="'+addressRelatedData+'"/></td>';
		$("#siteDetailTable").append(tableRow);
		if($("#noSite").size() > 0)
			$("#noSite").hide();
		siteAction();
	}else{
		var $container = $("tr[id='trId"+indexOpened+"']");
		$container.find("td").eq(0).html(siteNameOverlay);
		$container.find("td").eq(1).html(address1Overlay);
		$container.find("td").eq(2).html(address2Overlay);
		$container.find("td").eq(3).html(cityOverlay);
		$container.find("td").eq(4).html(stateOverlay);
		$container.find("td").eq(5).html(zipcodeOverlay);
		$container.find("input[type='hidden'][name$='siteName']").val(siteNameOverlay);
		$container.find("input[type='hidden'][name$='address1']").val(address1Overlay);
		$container.find("input[type='hidden'][name$='address2']").val(address2Overlay);
		$container.find("input[type='hidden'][name$='city']").val(cityOverlay);
		$container.find("input[type='hidden'][name$='state']").val(stateOverlay);
		$container.find("input[type='hidden'][name$='zipCode']").val(zipcodeOverlay);
		$container.find("input[type='hidden'][name$='addressRelatedData']").val(addressRelatedData);
		if($container.find("input[type='hidden'][name$='actionTaken']").val() != "insert")
			$container.find("input[type='hidden'][name$='actionTaken']").val("update");
	}
}

//resets overlay values
function resetOverlay(){
	$("#siteNameOverlay").val("");
	$("#address1Overlay").val("");
	$("#address2Overlay").val("");
	$("#cityOverlay").val("");
	$("#stateOverlay option").eq(0).attr("selected", "selected");
	$("#zipcodeOverlay").val("");
	$("label.error").remove();
}

//function performing action change task
function siteAction()
{
	$(".siteAction").change(function(){
		var seqId = $(this).attr("id").replace("action","");
		var optionSelected =  $(this).find("option:selected").val();
		if(optionSelected == "1"){
			var selectedRow = $("#trId"+seqId);
			$("#siteNameOverlay").val(selectedRow.find("td").eq(0).html());
			$("#address1Overlay").val(selectedRow.find("td").eq(1).html());
			$("#address2Overlay").val(selectedRow.find("td").eq(2).html());
			$("#cityOverlay").val(selectedRow.find("td").eq(3).html());
			var stateValue = selectedRow.find("td").eq(4).html();
			$("#stateOverlay option[value='"+stateValue+"']").attr("selected", "selected");
			$("#zipcodeOverlay").val(selectedRow.find("td").eq(5).html());
			$("#indexOpened").val(seqId);
			$(".overlay").launchOverlayNoClose($(".alertBoxAddSite"), "540px");
		}else if(optionSelected == "2"){
			$("tr[id='trId"+seqId+"']").hide();
			$("tr[id='trId"+seqId+"'] input[id$='actionTaken']").val("delete");
			if($("#siteDetailTable tr:visible").size() == 1){
				if($("#noSite").size() > 0){
					$("#noSite").show();
				}else{
					$("#siteDetailTable").append('<tr id="noSite"><td colspan="7">No sites have been entered...</td></tr>');
				}
			}
		}
		$(this).find("option").eq(0).attr("selected", "selected");
	});	
}

//returns space for blank or nulls
function returnSpace(str){
	var strVal = " ";
	if(str!=null && str!=""){
		strVal=str;
	}
	return strVal;
}

//checks if element is available
function checkIfElementAvailable(element){
	if(element == null || $(element).attr("id") == null)
		return false;
	else
		return true;
}


/*checks if element is enable
New Method in R4*/
function checkIfElementEnable(element){
	if(element == null || $(element).attr('disabled') == undefined || $(element).attr('disabled') != "disabled"){
		return false;
	}
	else{
		return true;
	}
}


//method that checks and updates the cost per unit service value
function updateCostPerUnit(){
	var totalNOS = $("#totalNumberOfService").val();
	var totalFR = $("#totalFundingRequest").val();
	if(totalNOS != "" && totalFR != ""){
		totalNOS = parseInt(totalNOS.replaceAll(",", ""));
		totalFR = parseFloat(totalFR.replaceAll(",", ""));
		if(totalNOS > 0 && totalFR > 0){
			$("#costPerUnit").val(Math.round(100*totalFR/totalNOS)/100);
			$("#costPerUnit").changeCurrency();
		}else{
			$("#costPerUnit").val("ERROR");
		}
	}else{
		$("#costPerUnit").val("");
	}
}