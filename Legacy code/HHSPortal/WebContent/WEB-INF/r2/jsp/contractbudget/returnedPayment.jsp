<%-- The jsp is added in Release 6 for return payment.
It will show the information of Returned Payment details in Contract Budget landing page.
 --%>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<%@ page errorPage="/error/errorpage.jsp"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@ page import="com.nyc.hhs.util.HHSUtil"%>


<script type="text/javascript">
/**
 * This function is called when add return payment is clicked
 * and validation on form fields is done
 */
function loadValidateinSuccess() {
	$("#addReturnedPaymentForm").validate({
		rules: {
            checkAmountVal: {
                  required: true,
                  },
                  descriptionInput: {
                        required: true,
                        },
                  
                  checkReceivedRadio: {
                        required: true,
                        },
                        
                  notifyProviderVal: {
                              required: true,
                              },
                   notProvider: {
                	   required:true
                   }            
      },
      messages: {
            checkAmountVal: {
                  required: "<fmt:message key='REQUIRED_FIELDS'/>",
                      },
                      descriptionInput: {
                              required: "<fmt:message key='REQUIRED_FIELDS'/>",
                                  },
                        checkReceivedRadio: {
                              required: "<fmt:message key='REQUIRED_FIELDS'/>",
                                    },
                        notifyProviderVal: {
                              required: "<fmt:message key='REQUIRED_FIELDS'/>",
                                    },
                        notProvider:{
                        	required:"<fmt:message key='REQUIRED_FIELDS'/>"
                        }            
      },
      submitHandler: function(form){
    	  pageGreyOut();
          document.addReturnedPaymentForm.action = $("#addReturnPaymentUrl").val()+"&budgetID=" + budgetID + "&checkAmountVal="
          + $('#checkAmountVal').val()+ "&checkReceivedRadio="
          + $("input[name='checkReceivedRadio']:checked").val() + "&notifyProviderVal="
          + $("input[name='notifyProviderVal']:checked").val() + "&contractID=" + contractID + "&notProvider=" + $('#notProvider').val() + "&targetUser=" + $('#notProvider').val()
          + "&fiscalYear=" + fiscalYearID +"&programName=" + $("#programName").val() + "&programId=" + $("#programId").val() + "&actionSelected=" + "remittanceRequest";
          var options = {
		          success : function(e) {
		          clearAndCloseOverLay();
		          showCBGridTabsJSP('returnedPayment',
		                      'returnedPaymentWrapper', '', '');
		          removePageGreyOut();
		          if(e.error ==1){
		                var _message= e.message;
		              $(".errorDivForContractBudget").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('errorDivForContractBudget', this)\" />");
		              $(".errorDivForContractBudget").removeClass("passed");
		              $(".errorDivForContractBudget").addClass("failed");
		              $(".errorDivForContractBudget").show();
		          }
		          else{
		                 if($("input[name='checkReceivedRadio']:checked").val() == 'Y'){
		                      var _message= "Returned Payment Review Task has been generated.";
		                    $(".errorDivForContractBudget").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('errorDivForContractBudget', this)\" />");
		                    $(".errorDivForContractBudget").removeClass("failed");
		                    $(".errorDivForContractBudget").addClass("passed");
		                    $(".errorDivForContractBudget").show();
		                }else if($("input[name='notifyProviderVal']:checked").val() == 'Y')
		                      {
		                      var _message= "Provider has been notified of the amount requested. Returned Payment is Pending Submission.";
		                      $(".errorDivForContractBudget").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('errorDivForContractBudget', this)\" />");
		                      $(".errorDivForContractBudget").removeClass("failed");
		                      $(".errorDivForContractBudget").addClass("passed");
		                      $(".errorDivForContractBudget").show();
		                      }
		                else{
		                var _message= "Returned Payment has been added in Pending Submission status.";
		                $(".errorDivForContractBudget").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('errorDivForContractBudget', this)\" />");
		                $(".errorDivForContractBudget").removeClass("failed");
		                $(".errorDivForContractBudget").addClass("passed");
		                $(".errorDivForContractBudget").show();
		                }
		          }},
		          error : function(xhr, ajaxOptions, thrownError) {
		            clearAndCloseOverLay();
		              showCBGridTabsJSP('returnedPayment',
		                          'returnedPaymentWrapper', '', '');
		              removePageGreyOut();
		            var _message= "This request could not be completed. Please try again in a few minutes.";
		              $(".errorDivForContractBudget").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('errorDivForContractBudget', this)\" />");
		              $(".errorDivForContractBudget").removeClass("passed");
		              $(".errorDivForContractBudget").addClass("failed");
		              $(".errorDivForContractBudget").show();
		          }
		    
		}
          $(document.addReturnedPaymentForm).ajaxSubmit(options);
          pageGreyOut();

      },
      errorPlacement: function(error, element) {
    	  error.appendTo(element.parents("div.row").find("span.error"));
      }
	});
};
	$("#unRecoupedAmount").jqGridCurrency();
	
	 $(".tableCheckAmountValue").each(function(e) {
		$(this).html(jqGridformatCurrency($(this).html()).replace('$',''));
	}); 
</script>

<portlet:resourceURL var='viewReturnedPayment' id="viewReturnedPayment"></portlet:resourceURL>

<portlet:resourceURL var='getNotificationProvider' id="getNotificationProvider" escapeXml="false">
</portlet:resourceURL>

<portlet:resourceURL var="getNotificationHistory" id="getNotificationHistory" escapeXml="false"></portlet:resourceURL>

<portlet:resourceURL var="launchReturnedPaymentOverlay" id="launchReturnedPaymentOverlay"
	escapeXml="false"></portlet:resourceURL>

<portlet:resourceURL var="cancelReturnedPayment"
	id="cancelReturnedPayment"></portlet:resourceURL>
	
<input type="hidden" id="launchReturnedPaymentOverlay"
	value="${launchReturnedPaymentOverlay}" />		
	
<input type="hidden" id="cancelReturnedPayment"
	value="${cancelReturnedPayment}" />
<input type="hidden" id="lsColumnName"
	value="${lsColumnName}" />
<portlet:resourceURL var="initiateReturnedPayment"
	id="initiateReturnedPayment"></portlet:resourceURL>
<input type="hidden" id="initiateReturnedPayment"
	value="${initiateReturnedPayment}" />
<input type="hidden" id="initiateReturnedPayment"
	value="${initiateReturnedPayment}" />

<input type='hidden' value='${viewReturnedPayment}'
	id='hiddenViewReturnedPayment' />
<input type="hidden" id="getNotificationHistoryUrl" value="${getNotificationHistory}"/>

<input type="hidden" id="getNotificationProvider" value="${getNotificationProvider}"/>

<c:if test="${(org_type eq 'agency_org') || (org_type eq 'city_org')}">
<!-- This is the Jsp for content of return payment accordian-->
<div class="formcontainer paymentFormWrapper">
	<div class="row">
		<span class="label" title="(Advances Disbursed - Advance Recoupment Amounts). The Advance Recoupment Amount accounts for recoupments added to invoices in Pending Approval and Approved status. The amount may change as the Agency completes invoice reviews.">Unrecouped Advance Amount:</span> <span
			class="formfield" id="unRecoupedAmount">${unrecoupedAdvAmount}</span>
	</div>
		<div class="row">
			<span class="label">Last Notified Date:</span> <span
				class="formfield"> <c:choose>
					<c:when test="${(lastNotifiedDate eq '--')}">
						<span class="formfield" id="lastNotifiedDate">--</span>
					</c:when>
					<c:otherwise>
						<a href="javascript:"
							onclick="javascript:getNotificationHistory();">${lastNotifiedDate}</a>
					</c:otherwise>
				</c:choose> <c:if test="${(lastNotifiedDate != '--')}">
				</c:if>
			</span>
		</div>
	</div>
	<div class="taskButtons" id="actionButtons" style="display: none; padding-top: 2px; padding-right: 200px;">
	<!-- Change for defect 8608-->
		<!-- [Start] QC9701 ReturnPaymentStatus -->
		<c:if test="${ReturnPaymentStatus eq '20'}">
			<input style="float: left;" type="button" class="add marginReset"
				value="Add Returned Payment" jspname='' id="returnedPaymentBttn"
				onclick="returnedPaymentConfirm();" /> 
			<input style="float: left;left: 5px;position: relative;"
				type="button" class="mail marginReset" value="Notify Provider" jspname=''
				id="bttnNotifyProvider" onclick="notifyProvider()" />
		</c:if>
	</div>
</c:if>

<input type='hidden' value='${totalApprovedRetPayAmount}'
	id='hiddenTotalApprovedAmount'/>

<div id="returnedPaymentListTable" class="tabularWrapper"><st:table
		objectName="returnedPaymentCheckList" cssClass="heading"
		alternateCss1="evenRows" alternateCss2="oddRows">
<!--  Fix for alignment of grid -->
		<%--  Added extension for enhancement 8652--%>
		<st:property headingName="Check#" columnName="checkNumber" size="7%">
		<st:extension
						decoratorClass="com.nyc.hhs.frameworks.grid.ReturnedPaymentAgencyActionExtension" />
		</st:property>
		<st:property headingName="Agency Tracking#"
			columnName="agencyTrackingNumber" size="16%">
		</st:property>

		<st:property headingName="Received Date" columnName="receivedDate"
			size="15%">
		</st:property>
		<st:property headingName="Approved By" size="18%"
			columnName="approvedBy">
		</st:property>
		<st:property headingName="Check Amount($)" columnName="checkAmount"
			size="19%">
			
			<st:extension
						decoratorClass="com.nyc.hhs.frameworks.grid.ReturnedPaymentAgencyActionExtension" />
		</st:property>
		<st:property headingName="Status" columnName="checkStatusName"
			size="15%">
		</st:property>
		<st:property headingName="Action" columnName="action" size="20%">
			<st:extension
				decoratorClass="com.nyc.hhs.frameworks.grid.ReturnedPaymentAgencyActionExtension" />
		</st:property>
	</st:table>
<c:if test="${fn:length(returnedPaymentCheckList) eq 0}">
	<div class="noRecordPaymentDiv noRecord" id="noRecordPaymentDiv">No Records Found</div>
	</c:if>

</div>
<!-- Including jsp Files -->
<jsp:include page="addReturnedPaymentOverlay.jsp"></jsp:include>
<jsp:include page="notificationHistoryOverlay.jsp"></jsp:include>
<jsp:include page="cancelReturnedPayment.jsp"></jsp:include>
<jsp:include page="notifyProviderOverlay.jsp"></jsp:include>