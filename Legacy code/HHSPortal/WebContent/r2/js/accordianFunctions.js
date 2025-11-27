	//Initialize for Collapse and Expand Demo:
	function displayAccordion(obj) {
		var _contextPath = "../../";
		if (document.getElementById("contextPathSession") != null && typeof($("#contextPathSession").val()) != "undefined"){
			_contextPath = "../";
		}

		if ($("#close").attr("checked") != 'checked') {
			var openShow = $(obj).next().attr("class");
			if (openShow == 'custDataRowHead1') {
				$(obj).next().removeAttr("class");
				$(obj).next().addClass("close");
				$(obj)
						.attr(
								"style",
								"background:url('"+_contextPath+"framework/skins/hhsa/images/arrowExpand.png') no-repeat scroll 99% center #4297E2");
				$(obj).nextAll().hide();
			} else {
				$(obj).next().removeAttr("class");
				$(obj)
						.attr(
								"style",
								"background:url('"+_contextPath+"framework/skins/hhsa/images/arrowCollapse.png') no-repeat scroll 99% center #4297E2");
				$(obj).next().addClass("custDataRowHead1");
				$(obj).nextAll().show();
			}
		}
	}
	 //This function collapse all the accordians
	function collapseExpandAll(expandCollapse) {
		var _contextPath = "../../";
		if (document.getElementById("contextPathSession") != null && typeof($("#contextPathSession").val()) != "undefined"){
			_contextPath = "../";
		}
	
		var topHeaderIds = $("div[class*='custToggleHead']");
		topHeaderIds
				.each(function() {
					if (expandCollapse == 'collapseAll') {
						$(this).next().removeClass();
						$(this).next().addClass("close");
						$(this).nextAll().hide();
						$(this)
								.attr(
										"style",
										"background:url('"+_contextPath+"framework/skins/hhsa/images/arrowExpand.png') no-repeat scroll 99% center #4297E2");
					} else {
						$(this).next().removeClass();
						$(this).next().addClass("custDataRowHead");
						$(this).nextAll().show();
						$(this)
								.attr(
										"style",
										"background:url('"+_contextPath+"framework/skins/hhsa/images/arrowCollapse.png') no-repeat scroll 99% center #4297E2");
					}
				});
	}
	//This function collapse all the accordians and apply the CCS to each accordian
	$(function() {
		var _contextPath = "../../";
		if (document.getElementById("contextPathSession") != null && typeof($("#contextPathSession").val()) != "undefined"){
			_contextPath = "../";
		}
		collapseExpandAll('collapseAll');
		var accordionHeaderIds = $("div[id*='accordionHeaderId']");
		accordionHeaderIds
				.each(function() {
					$(this)
							.css(
									{
										'background' : "url('"+_contextPath+"framework/skins/hhsa/images/arrowExpand.png') no-repeat scroll 99% center #4297E2",
										'cursor' : 'pointer',
										'background-color' : '#4297E2'
									});
					$(this).nextAll().hide();
				});
	});

	// This fuction will be executed on click of accordian 
	//and return boolean based on div is empty or not
 function divEmpty(divId){
		var returnVal = false;
		var htmlContent = $('#'+divId).html();
		if(trim(htmlContent)==""){
			returnVal = true;
		}
		return returnVal;
 }
