<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ page import="java.util.ArrayList"%>
<%@ taglib prefix="render" uri="http://www.bea.com/servers/portal/tags/netuix/render" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@page import="org.apache.poi.hssf.usermodel.*" %>
<%@page import="java.io.*" %>
<%@ page contentType="application/vnd.ms-excel" pageEncoding="ISO-8859-1"%> 
<portlet:defineObjects />

<%
String lsOrgType=(String)request.getSession().getAttribute("org_type");
String agencyId=(String)request.getSession().getAttribute("agencyId");
String procurementId=(String)request.getSession().getAttribute("procurementId");
String procurementTitle=(String)request.getSession().getAttribute("procurementTitle");
String epinId=(String)request.getSession().getAttribute("epinId");
String status=(String)request.getSession().getAttribute("status");
String errorMessage=(String)request.getSession().getAttribute("errorMessage");
%>


<script type="text/javascript">
	
	function submitApplication()
	{
		document.enterEpin.submit();
	}
		
	function downloadExel(procurementId)
	{		    
	    console.log("procurementId :: "+procurementId); 
		var xslsFileName = document.getElementById("nameOfFile").value;
		console.log("xslsFileName :: "+xslsFileName);  	
		var jqxhr = $.ajax( {
			url : $("#contextPathSession").val()+"/GetContent.jsp?action=getRfpReportXsls&xslsFileName="+xslsFileName+"&procurementId="+procurementId,
			type : 'POST',
			success : function(data) {
			 	if(data == 'FilenotFound'){
				 	console.log(data);
					var msg = "No Records Found";
					document.getElementById("errMessage").innerHTML = msg;
					
				}else{
				    var msg = " ";
					document.getElementById("errMessage").innerHTML = msg;
				  	window.location.href = $("#contextPathSession").val()+"/GetContent.jsp?action=getRfpReportXsls&xslsFileName="+xslsFileName+"&procurementId="+procurementId;
				}
			},
			error : function(data, textStatus, errorThrown) {
			},
			complete : function() {
			}
		});
		
	};

</script>

<c:set var="launchOverlay" value="true"></c:set>

	<h2 class='autoWidth'>RFP Proposal Status Report</h2>
	
	<div class="linkReturnVault alignRht">
		<a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_maintenancelanding&_nfls=false&app_menu_name=header_maintenance"
			>Return to Maintenance
			Main Page</a>
		<c:remove var="rfpEpinLst" scope="session"/>
	</div>
	<div class='hr'></div>

	<portlet:actionURL var="enterEpinUrl" escapeXml="false">
		<portlet:param name="submit_action" value="findRFP" /> 
	</portlet:actionURL>

	<form action="${enterEpinUrl}" method="post" id="enterEpin" autocomplete="off" name="enterEpin">
	
	<div id="selectEpinDiv" >
	 			
		<div class="tabularCustomHead">Enter Procurement EPIN</div>
		
		<p> Please Enter Procurement EPIN to find RFP </p>
		<div id="transactionStatusDiv" class=""></div>
		<div class="selectEpinBody">
				
				<input type="text" class="input" size="128" style="width:35%;" maxlength="128" name="epinEntry" id="epinEntry" autocomplete="off"/>
				<input type="button" onclick="submitApplication();" value="Find RFP" class="button" id="findrfp" >
				<input type="hidden"  name="nameOfFile"  id="nameOfFile"  value="${nameOfFile}" > 
			
		</div>
	 </div>
  
   	<div id="errMessage" style="color:red; font-size:13px; font-weight:bold; height:15px; width:100%; padding-bottom:5px">
   		<p style="color:red; font-size:13px; font-weight:bold;">${errMessage}</p>
   	</div>
   
  
    <div id="rfp" class="tabularWrapper">
           		<c:choose>
                  	<c:when test="${rfpEpinLst ne null and fn:length(rfpEpinLst)>0}">
		            	<table cellspacing="0" cellpadding="0"  class="grid">
		                  <tr>
		                  	<th class="heading" align="center" width="10%">
									<b><a id="agency"  title="Agency" class="sort-default" >Agency</a></b></th>
								<th class="heading" align="center" width="30%">
									<b><a id="procurement"  title="Procurement Title" class="sort-default">Procurement Title</a></b></th>
								<th class="heading" align="center" width="10%">
									<b><a id="epin"  title="EPIN" class="sort-default" >EPIN</a></b></th>
								<th class="heading" align="center" width="9%">
									<b><a id="status"  title="Status" class="sort-default">Status</a></b></th>
								<th class="heading" align="center" width="9%">
									<b><a id="action"  title="Action" class="sort-default">Action</a></b></th>
		                  </tr>
							<c:forEach var="rfpItem" items="${rfpEpinLst}">
								<tr>
									<td class="evenRows" align="center" >${rfpItem.agencyId}
										<input type="hidden" id="rfpId" name="rfpId" value="${rfpItem.procurementId}">
									</td>
									<td class="evenRows" align="center" >${rfpItem.procurementTitle}</td>
									<td class="evenRows" align="center" >${rfpItem.epinId}</td>
									<td class="evenRows" align="center" >${rfpItem.status}</td>
									<td class="evenRows" align="center" >
										<input type="button" onclick="downloadExel('${rfpItem.procurementId}');" value="Generate Report" class="button" id="report" >	
									</td>
									
						 		</tr>	
							</c:forEach>					 
		                </table>
		             </c:when>
		          	
		          </c:choose>   
	</div>
  </form>
