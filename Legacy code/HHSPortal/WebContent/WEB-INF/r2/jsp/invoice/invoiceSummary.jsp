<%-- This jsp is used for S330 Invoice summary --%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@page import="com.nyc.hhs.util.HHSUtil" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="javax.portlet.PortletContext"%>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="rule" uri="/WEB-INF/tld/rule-taglib.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/invoiceSummary.js"></script>
<style type="text/css">
	objHidden {display:none}
</style>
<div class="budgetSummary">
<c:set var="changeId" value="${subBudgetId}"></c:set>
	<%--code updation for R4 starts--%>
<c:set var="idVarTempFetchListInvoiceSummary" value="tabHighlightList${subBudgetId}" scope="session"/>
<input type="hidden" id="hdnTabHighlightList${subBudgetId}" name="hdnTabHighlightList${subBudgetId}" value="${sessionScope[idVarTempFetchListInvoiceSummary]}"/>
	<%--code updation for R4 ends--%>
<h3>Budget Summary</h3> 
		<table width="100%" cellspacing='0' cellpadding='0'>				
				<tr>
					<th colspan='2' class='alignCenter'>Line Item</th>
					<th class='alignCenter'>FY Budget</th>
					<th class='alignCenter'>YTD Invoiced Amount</th>
					<th class='alignCenter'>Remaining Amount</th>
					<th class='alignCenter'>Invoice Amount</th>
				</tr>
				<tr>
					<td width='3%' class='togglePlaceholder' id="togglerMain${changeId}" onclick="showme('taggingMain${changeId}', this.id);"><span>+</span></td>
					<td width='45%' class='bold'>${CBInvoiceSummary.totalCityFundedBudget.title}</td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalCityFundedBudget.approvedBudget}"/></label></td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalCityFundedBudget.ytdInvoicedAmount}"/></label></td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalCityFundedBudget.remainingAmount}"/></label></td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalCityFundedBudget.invoicedAmount}"/></label></td>
				</tr>
		</table>
			
	
			
			<div id='taggingMain${changeId}' style='display:none'>
			
		<%-- "Total Direct Costs" table Starts --%>			
			<table width="92%" cellspacing='0' cellpadding='0'>				
				<tr>
					<td width='40%' class='noBdr'><h3>${CBInvoiceSummary.totalDirectsCosts.title}</h3></td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalDirectsCosts.approvedBudget}"/></label></td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalDirectsCosts.ytdInvoicedAmount}"/></label></td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalDirectsCosts.remainingAmount}"/></label></td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalDirectsCosts.invoicedAmount}"/></label></td>
				</tr>
			</table>
		<%-- "Total Direct Costs" table Ends Here --%>		
			
			
		<%-- "Total Salary and Fringe" Table Starts --%>
			<table width="92%" cellspacing='0' cellpadding='0'>					
				<tr>
					<td width='3%' class='togglePlaceholder' id="toggler${changeId}" onclick="showme('tagging${changeId}', this.id);"><span>+</span></td>
					<td width='37%' class='bold'>${CBInvoiceSummary.totalSalaryAndFringesAmount.title}</td>
							<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalSalaryAndFringesAmount.approvedBudget}"/></label></td>
							<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalSalaryAndFringesAmount.ytdInvoicedAmount}"/></label></td>
							<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalSalaryAndFringesAmount.remainingAmount}"/></label></td>
							<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalSalaryAndFringesAmount.invoicedAmount}"/></label></td>		
				</tr>
					<tbody id="tagging${changeId}" style='display:none'>
					<tr >
						<td>&nbsp;</td>
							<td>${CBInvoiceSummary.totalSalary.title}</td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalSalary.approvedBudget}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalSalary.ytdInvoicedAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalSalary.remainingAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalSalary.invoicedAmount}"/></label></td>		
					</tr>
					<tr>
						<td>&nbsp;</td>
							<td><label>${CBInvoiceSummary.totalFringes.title}</td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalFringes.approvedBudget}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalFringes.ytdInvoicedAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalFringes.remainingAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalFringes.invoicedAmount}"/></label></td>		
					</tr>
				</tbody>
			</table>
			<%-- "Total Salary and Fringe" Table Ends Here --%>
			
			
			<%-- "Total OTPS" Table starts --%>
			<table width="92%" cellspacing='0' cellpadding='0' class='objHidden'>				
					<tr>
						<td width='3%' class='togglePlaceholder' id="toggler2${changeId}" onclick="showme('tagging2${changeId}', this.id);"><span>+</span></td>
						<td width='37%' class='bold'>${CBInvoiceSummary.totalOTPSAmount.title}</td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalOTPSAmount.approvedBudget}"/></label></td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalOTPSAmount.ytdInvoicedAmount}"/></label></td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalOTPSAmount.remainingAmount}"/></label></td>
					<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalOTPSAmount.invoicedAmount}"/></label></td>
					</tr>
					<tbody id='tagging2${changeId}' style='display:none'>
						<tr>
							<td>&nbsp;</td>
							<td>${CBInvoiceSummary.operationsAndSupportAmount.title}</td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.operationsAndSupportAmount.approvedBudget}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.operationsAndSupportAmount.ytdInvoicedAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.operationsAndSupportAmount.remainingAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.operationsAndSupportAmount.invoicedAmount}"/></label></td>		
						</tr>
					
					
						<tr>
							<td>&nbsp;</td>
							<td>${CBInvoiceSummary.utilitiesAmount.title}</td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.utilitiesAmount.approvedBudget}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.utilitiesAmount.ytdInvoicedAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.utilitiesAmount.remainingAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.utilitiesAmount.invoicedAmount}"/></label></td>		
						</tr>
					
					
						<tr>
							<td>&nbsp;</td>
							<td>${CBInvoiceSummary.professionalServicesAmount.title}</td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.professionalServicesAmount.approvedBudget}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.professionalServicesAmount.ytdInvoicedAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.professionalServicesAmount.remainingAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.professionalServicesAmount.invoicedAmount}"/></label></td>		
						</tr>
					
					
						<tr>
							<td>&nbsp;</td>
							<td>${CBInvoiceSummary.rentAndOccupancyAmount.title}</td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.rentAndOccupancyAmount.approvedBudget}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.rentAndOccupancyAmount.ytdInvoicedAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.rentAndOccupancyAmount.remainingAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.rentAndOccupancyAmount.invoicedAmount}"/></label></td>		
						</tr>
						
						<tr>
							<td>&nbsp;</td>
							<td>${CBInvoiceSummary.contractedServicesAmount.title}</td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.contractedServicesAmount.approvedBudget}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.contractedServicesAmount.ytdInvoicedAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.contractedServicesAmount.remainingAmount}"/></label></td>
							<td class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.contractedServicesAmount.invoicedAmount}"/></label></td>	
						</tr>
					
					</tbody>
			</table>
			<%-- "Total OTPS" Table Ends here --%>
			
			
			<%-- "Total Rate Based" table Starts --%>			
			<table width="92%" cellspacing='0' cellpadding='0' class='summaryWrapper2'>				
				<tr>
						<td width='40%' class='bold'>${CBInvoiceSummary.totalRateBasedAmount.title}</td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalRateBasedAmount.approvedBudget}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalRateBasedAmount.ytdInvoicedAmount}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalRateBasedAmount.remainingAmount}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalRateBasedAmount.invoicedAmount}"/></label></td>
				</tr>
			</table>
		<%-- "Total Rate Based" table Ends Here --%>	


		<%-- "Total Milestone Based" table Starts --%>			
			<table width="92%" cellspacing='0' cellpadding='0' class='summaryWrapper2'>				
				<tr>
						<td width='40%' class='bold'>${CBInvoiceSummary.totalMilestoneBasedAmount.title}</label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalMilestoneBasedAmount.approvedBudget}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalMilestoneBasedAmount.ytdInvoicedAmount}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalMilestoneBasedAmount.remainingAmount}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalMilestoneBasedAmount.invoicedAmount}"/></label></td>
				</tr>
			</table>
		<%-- "Total Milestone Based" table Ends Here --%>	


		<%-- "Unallocated Funds" table Starts --%>			
			<table width="92%" cellspacing='0' cellpadding='0' class=''>				
				<tr>
						<td width='40%' class='bold'>${CBInvoiceSummary.unallocatedFunds.title}</td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.unallocatedFunds.approvedBudget}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.unallocatedFunds.ytdInvoicedAmount}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.unallocatedFunds.remainingAmount}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.unallocatedFunds.invoicedAmount}"/></label></td>
				</tr>
			</table>
		<%-- "Unallocated Funds" table Ends Here --%>

		<h3 style='margin-left:8%;'>Total Indirect Costs</h3>
		<p style='margin-left:7%;'>
			&nbsp;&nbsp;<b>Indirect Rate - City Funded</b> &nbsp;
 ${asIndirectRate}  %
		</p>
		
		<%-- "Total Indirect Costs" table Starts --%>			
			<table width="92%" cellspacing='0' cellpadding='0'>				
				<tr>
						<td width='40%' class='bold'>${CBInvoiceSummary.totalIndirectCosts.title}</td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalIndirectCosts.approvedBudget}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalIndirectCosts.ytdInvoicedAmount}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalIndirectCosts.remainingAmount}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalIndirectCosts.invoicedAmount}"/></label></td>
				</tr>
			</table>
		<%-- "Total Indirect Costs" table Ends Here --%>	
		
			</div>
			
			<hr>
			
			<table width="100%" cellspacing='0' cellpadding='0'>				
				<tr>
					<td width='3%'>&nbsp;</td>
						<td width='45%' class='bold'>${CBInvoiceSummary.totalProgramIncome.title}
						<div>(Excluded from City Funded Budget; Not Invoiced)</div>
					</td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalProgramIncome.approvedBudget}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalProgramIncome.ytdInvoicedAmount}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalProgramIncome.remainingAmount}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalProgramIncome.invoicedAmount}"/></label></td>
				</tr>
		    </table>
			
			
			<table width="100%" cellspacing='0' cellpadding='0'>				
				<tr>
				
					<td width='48%' class='bold'>${CBInvoiceSummary.totalProgramBudget.title}  
						<div>(City Funded Budget + Program Income)</div>
					</td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalProgramBudget.approvedBudget}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalProgramBudget.ytdInvoicedAmount}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalProgramBudget.remainingAmount}"/></label></td>
						<td width='13%' class='right'><label><fmt:formatNumber type="currency" value="${CBInvoiceSummary.totalProgramBudget.invoicedAmount}"/></label></td>
				</tr>
		    </table>	
		    
		    		        
		    
		    <%--Start changes for enhancement id 6484 release 3.6.0 --%>
		   
		    <c:if test="${!recordBeforeRelease}">
		    <h3>Service Site Information</h3>
			<p style="padding:0px!important;">Please enter an address for each site where your organization proposes to deliver services.</p>
			
			
			<div class="tabularWrapper">
					<table width="100%" cellspacing='0' cellpadding='0' border="1" id="siteDetailTable${subBudgetId}">
						<tr class="tableRow${subBudgetId}">
							<th>Site Name</th>
							<th>Address 1</th>
							<th>Address 2</th>
							<th>City</th>
							<th>State</th>
							<th>Zip Code</th>
							<c:if test="${(lsSubBudgetStatusId eq '84' or lsSubBudgetStatusId eq '83') and (org_type eq 'provider_org')}">
								<th>Action</th>
							</c:if>
						</tr>
						<c:choose>
							<c:when test="${empty SiteDetailsBean}">
								<tr id="noSite${subBudgetId}">
									<td colspan="7">No sites have been entered...</td>
								</tr>
							</c:when>
							<c:otherwise>
								<c:forEach var="siteIterator" items="${SiteDetailsBean}" varStatus="item">
									<tr id="trId${subBudgetId}${item.index}" class="trClass${subBudgetId}${item.index}">
										<td class="siteName">${siteIterator.siteName}</td>
										<td class="address1">${siteIterator.address1}</td>
										<td class="address2">${siteIterator.address2}</td>
										<td class="city">${siteIterator.city}</td>
										<td class="state">${siteIterator.state}</td>
										<td class="zipCode">${siteIterator.zipCode}</td>
										<input type="hidden" class="actionTaken" />
										
										<c:if test="${(lsSubBudgetStatusId eq '84' or lsSubBudgetStatusId eq '83') and (org_type eq 'provider_org')}">
											<td>
												<select id="action${subBudgetId}${item.index}" disabled="disabled" class="siteAction${subBudgetId}${item.index} resetDropDown" onchange="siteAction(${subBudgetId},this,${item.index},'${hdnTabId}',${parentSubBudgetId})">
													<option value="0">I need to... </option>
												</select>
												
											</td>
									</tr>
									</c:if>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</table>
				</div>
			</c:if>	
	</div>

	

		    
		    <%-- End changes for enhancement id 6484 release 3.6.0 --%>
			
		
