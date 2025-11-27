<!-- This page is displayed when user click on view printer friendly button.
It will display printer friendly detail of application-->
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<style>
.tableHeaderPrint{
	 background-color: #E4E4E4;
}
.tableRowEvenPrint{
	background-color: #FFFFFF;
}
.tableRowOddPrint{
 	background-color: #F2F2F2;
}
.content{
	padding-top: 5px;
}
.content td{
	width: 300px;
	padding-left: 10px;
	
}
.content table{
	width: 80%;
}
.content table.documentTablePrint{
	width: 90%;
}
.content th{
	font-weight: bold;
	padding-left: 10px;
}
.noContent{
	border:1px solid grey;
	padding: 5px 0 5px 10px;
}
table{
	padding-bottom:20px;
}
.questions{
    text-align: right;
	background: none repeat scroll 0 0 #F2F2F2;
    margin: 0 8px 3px 0;
    border-top: 2px solid #ffffff;
}
.headingFont{
	font-wight:bold;
	font-size: 18px;
}
.sectionHeading{
	font-size: 16px;
}
.bodycontainer{
 	background: none repeat scroll 0 0 #FFFFFF;
    padding: 0px;
    width: auto;
}
br{
	display:none
}
td br{
	display: block;
}
.print-heading{
	background: none;
	font-size:17px;
	font-weight:bold;
	text-decoration:underline;
}
.headingText{
	border-bottom: 2px solid black;
    margin-bottom: 3px;
    padding-top: 7px;
    line-height:22px
}
.subheading{
	font-weight: bold;
}
</style>
<script>
	$(document).ready(function(){
		$(".questions").each(function(){
			$(this).height($(this).parent().height());
		});
	});
</script>

<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.BA_S062_PAGE, request.getSession())){%>
<!-- Body Wrapper Start -->
	<c:if test="${content.data ne null}">
	<div id="main-wrapper">
	<!-- HHS Header Start -->
		<div class="hhs_header">
			<table>
				<tr>
					<td width="187px">
						<table>
							<tr>
								<td><img src="../framework/skins/hhsa/images/HHS_logo_gray.png" /></td>
							</tr>
						</table>
					</td>
					<td width="527px">
						<table>
							<tr>
							<c:choose>
								<c:when test="${applicationType ne null && applicationType eq 'service' }">
									<td colspan="2" class="print-heading">HHS Accelerator Service Application</td>
								</c:when>
								<c:otherwise>
									<td colspan="2" class="print-heading">HHS Accelerator Business Application</td>
								</c:otherwise>
							</c:choose>
							</tr>
							<tr>
								<td class="print-td" width="300px"><b>Organization: </b>${content.ORGANIZATION_LEGAL_NAME}</td>
								<c:choose>
									<c:when test="${content.MODIFIED_BY ne null and 
										content.APPLICATION_STATUS ne 'Draft' and
										content.APPLICATION_STATUS ne 'draft' }">
										<td class="print-td" width="300px"><b>Submitted by: </b>${content.MODIFIED_BY}</td>
									</c:when>
									<c:otherwise>
										<td class="print-td" width="300px"><b>Submitted by: </b>N/A</td>																		
									</c:otherwise>
								</c:choose>
							</tr>
							<tr>
								<td class="print-td capitalize"><b>Status: </b>${content.APPLICATION_STATUS}</td>
								<c:if test="${content.APPLICATION_STATUS eq '<%=ApplicationConstants.APPROVED_STATE>'}">
									<td class="print-td"><b>Approved On: </b>${content.MODIFIED_DATE}</td>
								</c:if>
								<c:if test="${content.APPLICATION_STATUS ne '<%=ApplicationConstants.APPROVED_STATE>'}">
									<td class="print-td"><b>Last Modified: </b>${content.MODIFIED_DATE}</td>
								</c:if>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</div>
		<!-- HHS Header End -->
		<div class="content wordWrap">
			${content.data}
		</div>
	</div>
	</c:if>
	<c:if test="${content.data eq null}">
		${content.error}
	</c:if>
<% } else {%>
	<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
<%} %>
