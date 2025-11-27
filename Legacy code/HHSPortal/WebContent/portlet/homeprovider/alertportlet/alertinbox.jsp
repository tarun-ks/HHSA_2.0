<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="com.nyc.hhs.frameworks.grid.*"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="../resources/js/calendar.js"></script>
<portlet:defineObjects/>
<style>
#documentValuePop .label{width:35%;}
.taskreassign{
	margin-top: -20px;
	margin-top: -10px\9; /* IE8 and below */  
}
</style>
<script type="text/javascript"> 
//Start : R5 Added
$(document).ready(function(){UpdateAlertInbox();});
//End : R5 Added
function enabledisablebutton()
{				
	var chks = document.getElementsByName('check');
    var chkall = document.getElementsByName('selectAll');
    var hasChecked = false;
    for (var i = 0; i < chks.length; i++)
	{
    	if (chks[i].checked)
        {	 	
			document.getElementById("selectAll").checked = false;             
            hasChecked = true;
         }
	}
		if(hasChecked )
        {
			document.getElementById("delete").disabled = false;
			}else
         	{
        	 	document.getElementById("delete").disabled = true;        	 	
			}
	}
	
function deleteAlert()
{
	var chks = document.getElementsByName('check');
    var hasChecked = false;   
    var id="";  
    for (var i = 0; i < chks.length; i++)
    {
		if (chks[i].checked)
	    {	          	          	
	        id=id+chks[i].value+"|";          	          	
		}
    }
	id = id.substring(0, id.length-1);   	 
   	document.alertform.action = document.alertform.action + '&next_action=deleteMany&notificationIds='+id; 
	document.alertform.submit();
}

function submitForm(Id)
{	
	document.alertform.action = document.alertform.action + '&next_action=showdetails&notificationId='+Id; 
	document.alertform.submit();
}

function setVisibility(id, visibility) 
{
	document.getElementById(id).style.display = visibility;
}

function filtertask()
{    
	  var isValid = true;
	  $("input[type='text']").each(function(){
	        if($(this).attr("validate")=='calender'){
	              if(verifyDate(this)){
	                    var fromDate = $("#datefrom").val();
	                    var toDate = $("#dateto").val();
	                    if (Date.parse(toDate) < Date.parse(fromDate)) {
	                          $("#dateRange").html('! This range is not valid');
	                          isValid = false;
	                          return false;
	                    }
	              }else{
	                isValid = false;
	              }
	        }
	  });
	  
	  if(isValid){
	     pageGreyOut();
	     document.alertform.action=document.alertform.action+"&next_action=applyFilter&next_Task=submit";
		 document.alertform.submit();
	  }
}

function clearfilter()
{
	document.getElementById('alerttype').value="";	
	document.getElementById('datefrom').value="";
	document.getElementById('dateto').value="";	
	$("input[type='text']").each(function(){
		 if($(this).attr("validate")=='calender'){
			 $(this).parent().next().html("");
		 }
	});
}

function paging(node)
{      
	var OpenPage=document.getElementById('next_open').value;	             
    if(OpenPage=="showpage")
    {
 		document.alertform.action=document.alertform.action+"&next_action=showpage&nextPage="+node+"&previousParent="+parent;
    }
    else
    {
    	document.alertform.action=document.alertform.action+"&next_action=applyFilter&nextPage="+node;
    }
    document.alertform.submit();
}

function selectAllCheck()
{
	if(document.forms[0].selectAll.checked==true)
   	{        
    	if(document.forms[0].check.length==undefined)
       	{
       		document.forms[0].check.checked = true;
       	}
        for (var a=0; a < document.forms[0].check.length; a++)
        { 
           document.forms[0].check[a].checked = true;            
   	    }
   		document.getElementById("delete").disabled = false;
	}
    else
    {       	 
		if(document.forms[0].check.length==undefined)
        {
			document.forms[0].check.checked = false;
        }
        for (var a=0; a < document.forms[0].check.length; a++)
        {
             document.forms[0].check[a].checked = false;           
        }
   	    document.getElementById("delete").disabled = true;
	} 
}

</script>
<input type="hidden" id="next_open" name="next_open" value=${next_open} />
<form name="alertform" action="<portlet:actionURL/>" method ="post" style="min-height:250px;">
	<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S027_PAGE_2, request.getSession())) {%>
   		<h2>Alerts Inbox</h2>
   		<div class='iconQuestion'><a href="javascript:void(0);" title="Need Help?" onclick="pageSpecificHelp('Alerts/Notifications');"></a></div>
   		<div class="overlay"></div>
		<div class="alert-box-help">
			<div class="content">
	             <div id="newTabs" class='wizardTabs'>
	             	<div class="tabularCustomHead">Alerts/Notifications - Help Documents</div>
	             	<div id="helpPageDiv"></div>
	             </div>
			</div>
	       	<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
		</div>
		<div class="alert-box-contact">
			<div class="content">
				<div id="newTabs">
					<div id="contactDiv"></div>
				</div>
			</div>
		</div>
	  	Listed below are the alerts for your organization.Click on the alert subject to open the alert.<BR>
	  	<font color="red">In-system alerts will expire after 365 days and shall be automatically removed from your Alerts Inbox.</font>
		<div class="tasktopfilter" >
			<div class="taskfilter taskButtons">
				<input type="button" value="Filter Alerts"  title="Filter Alerts" class="filterDocument"  onclick="setVisibility('documentValuePop', 'inline');" />
				<!-- Popup for Filter Task Starts -->
				<div id="documentValuePop"  class='formcontainer'>
					<div class='close'>
						<a href="javascript:setVisibility('documentValuePop', 'none');" >X</a>
					</div>
						
					<div class='row'>
						<span class='label'>Alert Type:</span>
						<span class='formfield'>
							<select name="alerttype" id="alerttype">
								<c:out value="${filterList.msSelectedAlertType}"/>																				
									<c:forEach var="category" items="${filterList.msAlertTypeList}" >
										<%String selected = "";%>
										<c:if test="${category==filterList.msSelectedAlertType}">												 
											<%selected = "selected";%>										
										</c:if>
										<option  value="<c:out value="${category}"/>"  <%=selected%>> <c:out value="${category}"/></option>
									</c:forEach>
							</select>
						</span>
					</div>
																
					<div class='row'>
						<span class='label'>Date Received from:</span>
						<span class='formfield'>							
								<input type="text" style='width:78px;' name="datefrom" id="datefrom" validate="calender" maxlength="10" value="${filterList.msFromFilterDate}"/>
								<img src="../framework/skins/hhsa/images/calender.png" title="Received From Date" onclick="NewCssCal('datefrom',event,'mmddyyyy');return false;"/> 
								&nbsp;&nbsp;
						</span>
						<span class="error" id="dateRange" style='width:auto;'></span>
					</div>
					<div class='row'>
						<span class='label'>Date Received to:</span>
						<span class='formfield'>							
								<input type="text" style='width:78px;' name="dateto" id="dateto" validate="calender" maxlength="10" value="${filterList.msToFilterDate}"/>
								<img src="../framework/skins/hhsa/images/calender.png" title="Received To Date" onclick="NewCssCal('dateto',event,'mmddyyyy');return false;"/>
						</span>
						<span class="error" id="dateRange" style='width:auto;'></span>
					</div>
					<div class="buttonholder">
						<input type="button" class="graybtutton" value="Clear Filters" title="Clear Filters" onclick="clearfilter();" />
						<input type="button" value="Filter" name="filter" id='filtersBtn' title="Filter" onclick='filtertask()'/>
					</div>
					
				</div>Alerts: ${rowCount}
			</div>
			<div  class="taskreassign">
				<input type="button" id="delete" value="Delete" title="Delete" class=""  onclick="deleteAlert();" disabled="disabled" />
			</div>
		</div>
		<div class="clear"></div>	
		<div  class="tabularWrapper">
			<st:table   objectName="itemList" pageSize='<%=(Integer)session.getAttribute("allowedObjectCount")%>' cssClass="heading"
			alternateCss1="evenRows" alternateCss2="oddRows" >
				<st:property headingName="Select" columnName="msNotificationName" align="left" size="5%">
					<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.AlertInboxExtension" />
				</st:property>
							
				<st:property headingName="Alert Subject" columnName="msNotificationName" align="left" size="70%">
					<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.AlertInboxCheckExtension" />
				</st:property>
							
				<st:property headingName="Date Received"  columnName="msNotificationDate" align="center" size="25%" >																																				
					<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.AlertInboxCheckExtension" />
				</st:property>																				
			</st:table>
		</div>
	Alerts: ${rowCount}
	<%}else{ %>
 		<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
 	<%} %>
</form>
