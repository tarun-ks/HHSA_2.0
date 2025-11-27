<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects />
<script type="text/javascript">
	//Created for R4: This method is invoked to submit form on Login - Select an Organization screen /Overlay
	function submitApplication(){
		pageGreyOut();
		document.chooseOrganization.submit();
	}
	
	//This method is used to switch account for Multiple Organization.
	function switchAccount(){
		pageGreyOut();
		document.chooseOrganization.action = document.chooseOrganization.action	+ 'Overlay';
		var options = {
			success : function(e, statusText, xhr) {
				removePageGreyOut();
				if (e != null) {
					var msg = e.split("#");
					if (msg[0] == "failure") {
						$("#transactionStatusDiv").html(msg[1]);
						$("#transactionStatusDiv").addClass('failed');
						$("#transactionStatusDiv").show();
					}
				}
			},
			error : function(xhr, ajaxOptions, thrownError) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		};
		pageGreyOut();
		$(document.chooseOrganization).ajaxSubmit(options);
		return false;
	}
	
	$(document).ready(function(){
		onReadyMethod();
	});
	$(document).keyup(function(e) {

		  if (e.keyCode == 27) 
		  {
			  $("#optionsBox").hide();
			}   // esc
		});
	
	//This method creates the Combobox feature for Select an Organization Screen. 
	//Create all the actions and events handled by the typeahead combobox. This method also makes check to enable/disable login button.
	function onReadyMethod(){
		//SUMA: Combobox
		var inputBox = $("#typeheadbox");
		var offset = inputBox.offset();
		var leftToSubtract = 0;
		if($(".alert-box-switchAccountDiv").is(":visible")){
			leftToSubtract = $(".alert-box-switchAccountDiv").offset().left;
		}
		$("#optionsBox").css({
			top: offset.top + inputBox.offsetHeight + 3,
			left: (offset.left) - leftToSubtract
		});
		
		$("#combotable_button").unbind("click").click(function(e){
			$("#optionsBox").toggle();
			e.stopPropagation();
		});
		$(document).click(function(){
			$("#optionsBox").hide();
		});
		
		
		inputBox.unbind("keyup").unbind("click").unbind("keydown").keyup(function(e){
			var key = e.keyCode;
			$('#login').attr('disabled', 'disabled');
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
		
		$("#dropdownul li").unbind("click").unbind("mouseover").unbind("mouseout").click(function(e){
			$("#organizationIdKey").val($(this).attr('key'));
			inputBox.val($(this).text());
			$("#optionsBox").hide();
			enableDisableLoginButton();	
			e.stopPropagation();
		}).mouseover(function(){
			$("#dropdownul li").removeClass("selectLiCombo");
			$(this).addClass("selectLiCombo");
		}).mouseout(function(){
			$(this).removeClass("selectLiCombo");
		});
		
		 // Switch Account Cancel Button
		$(".alert-box").find('#cancelSwitchAccount').unbind("click").click(function() {
			$(".overlay").closeOverlay();
			return false;
		});
		
		 //On change of combobox list value
		 $('#orgDetails').change(
            function (e) {
         		enableDisableLoginButton();	            	
         });
	}	
	
	//Created for R4: This method Enables/Disables Login Button
	function enableDisableLoginButton()
	{
		if(null != $('#typeheadbox').val())
			$('#login').removeAttr('disabled');
		else
		$('#login').attr('disabled', 'disabled');
	}
</script>

<c:if test="${launchOverlay ne null and launchOverlay eq 'true'}">
	<c:set var="launchOverlay" value="true"></c:set>
</c:if>
<c:if test="${launchOverlay eq null or launchOverlay ne 'true'}">
	<c:set var="launchOverlay" value="false"></c:set>
</c:if>
<c:if test="${launchOverlay ne 'true'}">
<!--NYC Header-->
<div id="nyc_header_div" class="hhs_header">
	<h1 title="HHS Accelerator">Human Health Services Accelerator</h1>
	<div class="toolbar">
		<div class="textresize">
			<ul>
		        <li><a onclick="changemysize(this, 10);" href="javascript:void(0);"  title="Small Text Size" id="smallA">A</a></li>
		        <li><a onclick="changemysize(this, 12);" href="javascript:void(0);"  title="Medium Text Size" id="mediumA" class="textmedium">A</a></li>
		        <li><a onclick="changemysize(this, 14);" href="javascript:void(0);"  title="Large Text Size" id="largeA" class="textbig">A</a></li>
		    </ul>
		    <input type='hidden' name='aaaValueToSet' id='aaaValueToSet' value='${aaaValueToSet}' />
		    <input type='hidden' name='urlForAAA' id='urlForAAA' value='${pageContext.servletContext.contextPath}/saveAAASize?next_action=aaaValueToSet' />
		    <span>Text Size:</span>
		</div>
	</div>
</div>
<!--NYC Header Ends-->
</c:if>

	<portlet:actionURL var="chooseOrganizationUrl" escapeXml="false">
		<portlet:param name="submit_action" value="multiAccount" />
	</portlet:actionURL>
<div id="selectOrgDiv" >
	<form action="${chooseOrganizationUrl}" method="post" id="chooseOrganization" autocomplete="off" name="chooseOrganization">
		<div class="notify"> 
			<c:choose>
				<c:when test="${errorMsg ne null }">
					<div class="failed">${errorMsg }</div>
				</c:when>
				<c:when test="${userActivated ne null }">
					<div class="failed">${userActivated }</div>
				</c:when>
			</c:choose>
		</div>
		<div id="selectOrganizationContainer">
			<c:choose>
				<c:when test="${launchOverlay eq 'true'}">
					<div class="tabularCustomHead">Login to a Different Organization</div>
				</c:when>
			</c:choose>
			<div style="padding: 10px">
				<h2>Select an Organization</h2>
				<div class='hrBold'></div>
				<c:if test="${message ne null}">
					<div class="${messageType}" id="messagediv" style="display:block">${message} <img
						src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
						class="message-close" onclick="showMe('messagediv', this)">
					</div>
					<div>&nbsp;</div>
				</c:if>
				<span>
					<c:choose>
						<c:when test="${launchOverlay ne 'true'}">
							<p>Please use the type ahead field or click on the drop down arrow below to select the organization you would like to log in to. 
							<br />If you want to log into a different organization after your selection below, you can click the Switch Organization icon in the top right corner.</p>
						</c:when>
						<c:otherwise>
							<p>Please use the type ahead field or click on the drop down arrow below to select the organization you would like to log in to.
							<br />
							<p style="color:#FF0000">Please close any open HHS Accelerator browser sessions before continuing.
							</p><br />
							Click the Cancel button to return to the previous screen.</p>
						</c:otherwise>
					</c:choose>
				</span>
				<div id="transactionStatusDiv" class=""></div>
				<div class="selectOrganizationBody">
					<div class="tabularWrapperOverlay" style="float:left;">
						 <div class="formcontainerOverlay">
						 	 <div class="row" style="width:300px">
								<span class="label" style="width:100%">Select/Search for Organization:</span>
							</div>
						</div> 
					</div>
					<div class="ddcombo" id="box1">
						<table cellspacing="0" cellpadding="0" border="0" class="ddcombo_table" id="combotable"><tbody>
							<tr>
								<td class="ddcombo_td1">
									<div class="ddcombo_div4" style="background: url(&quot;../framework/skins/hhsa/images/transparent_pixel.gif&quot;) repeat scroll 0% 0% transparent;">
										<input class="ddcombo_input1 ddcombo_input" id="typeheadbox" name="typeheadbox" value="" style="color: gray; background: url(&quot;../framework/skins/hhsa/images/transparent_pixel.gif&quot;) repeat scroll 0% 0% transparent;" ddcombo_autocomplete="off">
										<input type="hidden" name="organizationIdKey" id="organizationIdKey"/>
									</div>
								</td>
								<td valign="top" align="left" class="ddcombo_td2" id="combotable_button"><a></a><img src="../framework/skins/hhsa/images/button2.png" style="display: none;"></td>
							</tr></tbody>
						</table>
					</div>
					<div style="display:none; position: absolute; width: 271px;" class="ddcombo_results" id="optionsBox">
						<ul id= "dropdownul" style="max-height: 180px; overflow: auto;">
							<c:forEach items="${orgDetailsMap}" var="entry">
						        <li class="ddcombo_event data" key="${entry.key}" id="li_${entry.key}">${entry.value}</li>
						    </c:forEach>
						</ul>
					</div>
						<c:choose>
							<c:when test="${launchOverlay ne 'true'}">
								<div class="buttonholder" style="margin-right: 324px;">
									<input type="button" value="Cancel" type="reset" class="graybtutton" onclick="location.href='${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_login_page&_nfls=false&logout=logout'"/>
							</c:when>
							<c:otherwise>
								<div class="buttonholder" style="margin-right: 20px;">
									<input type="button" value="Cancel" name="cancel" id="cancelSwitchAccount" class="graybtutton" />
									<!-- input value="Login" type="button" class="button" id="login" name="login" title="Login" onclick="switchAccount();"  /> -->
							</c:otherwise>
						</c:choose>
						<input value="Login" type="button" class="button" id="login" name="login" onclick="submitApplication();" disabled="disabled"  />
					</div>
				</div>
			</div>
		</div>
	</form>
</div>
