<%-- This JSP file is for generating Procurement Certification of Funds Document S410.%--%>

<%--
Below HTML tags are used for generating Table with <TR> <TD> elements, to display Procurement Certification of funds
read only document.
Grid data for "Chart of Accounts Allocation" and "Funding Source Allocation" along with header details are iterated
in <TR> <TD>. 
Header and Account details with FY are pre-populated in ProcurementCOF bean from the ConfigurationService class and
RFPReleaseController class.
Fields are used here in HTML tags to display the document. Account Details with FY's are iterated using <TR> <TD> tags 
with an array list of same bean fields to show "Chart of Accounts Allocation" and "Funding Source Allocation" details.
--%>

<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="com.nyc.hhs.model.AccountsAllocationBean"%>
<%@page import="org.apache.commons.beanutils.BeanUtils"%>
<%@page import="com.nyc.hhs.model.FundingAllocationBean"%>
<%@page import="com.nyc.hhs.model.ProcurementCOF"%>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/grid.locale-en.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jqgridDialogOveride.js"></script>

<h2>Procurement Certification of Funds</h2>
			<div class='hr'></div>
			<c:if test="${message ne null}">
				<div class="${messageType}" id="messagediv" style="display:block">${message}</div>
			</c:if>
			<div><div>
							<%
							ProcurementCOF procCOF = (ProcurementCOF)request.getAttribute("ProcurementCOF");
							%>
<%-- Form Data Starts --%>
<div class="formcontainer">
 <div class="row">
      <span class="label">Agency:</span>
      <%String[] temp = procCOF.getAgencyName().split(" - ");
      %>
      <span class="formfield"><%= temp[1] + " (" + temp[0] + ")"%></span> 
      
 </div>
  <div class="row">
      <span class="label">Agency Code:</span>
      <span class="formfield"><%=procCOF.getAgencyCode() %></span>
    </div>  
	<div class="row">
      <span class="label">Procurement Value:</span>
      <span class="formfield">
      <label id="ProcurementVal"><%=procCOF.getOrigContractValue()%></label></span>
    </div>
	<div class="row">
      <span class="label">Procurement Title:</span>
      <span class="formfield"><%=procCOF.getProcurementTitle() %></span>
    </div>
	<div class="row">
      <span class="label">Procurement E-PIN:</span>
      <span class="formfield"><%=procCOF.getProcEpin() %></span>
    </div>
	<div class="row">
      <span class="label">Contract Start Date:</span>
      <span class="formfield"><%=procCOF.getOrigContractStartDate()%></span>
    </div>
    <div class="row">
      <span class="label">Contract End Date:</span>
      <span class="formfield"><%=procCOF.getOrigContractEndDate()%></span>
    </div>
	<div class="row">
      <span class="label">Submitted By:</span>
      <span class="formfield"><%=procCOF.getUserFirstName() + " " + procCOF.getUserLastName()%></span>
    </div>
	<div class="row">
      <span class="label">Date Submitted:</span>
      <span class="formfield"><%=procCOF.getCreatedDate() %></span>
    </div>
	<div class="row">
      <span class="label">Approved By:</span>
      <span class="formfield"><%=procCOF.getApproverFirstName() + " " + procCOF.getApproverLastName() %></span>
    </div>
	<div class="row">
      <span class="label">Date Approved:</span>
      <span class="formfield"><%=procCOF.getApprovedDate() %></span>
    </div>
</div>

<p>&nbsp;</p>
<h3>Chart of Accounts Allocation</h3>
		<%-- Table Starts --%>
		<div class="tabularWrapper" style="overflow-x:auto">
			<table cellspacing='0' cellpadding='0' border="1">
				<tbody>
					<tr>
						<th class='center' width='25%'>Chart of Accounts</th>
						<%
							ArrayList headerArr = (ArrayList)request.getAttribute("HeaderList");
							for(int i=0;i<headerArr.size();i++)
							{
								%>
								<th class='center' width=''>
								<%= (String)headerArr.get(i) %> 
								</th>
							<%	
							}
						%>
						<th class='center' width=''>Total</th>
					</tr>
					
					<tr>
								
						<td>Overall</td>
					
						<%
							ArrayList COADetailArr = (ArrayList)request.getAttribute("DetailList");
							AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
							HashMap<Integer, List<String>> myMapCOA = new HashMap<Integer, List<String>>(); 
							for(int i=0;i<COADetailArr.size();i++)
							{
								List<String> listAmount = new ArrayList<String>();
								loAccountsAllocationBean = (AccountsAllocationBean) COADetailArr.get(i);
								BigDecimal fiscalAmountTotal = BigDecimal.ZERO;
								for(int j=1; j<= headerArr.size();j++){
										String methodName = "fy" + j;
										String fiscalAmount = "";
										fiscalAmount = (String) BeanUtils.getProperty(loAccountsAllocationBean, methodName);
										fiscalAmountTotal = fiscalAmountTotal.add(new BigDecimal(fiscalAmount));
										//fiscalAmountTotal +=  Double.valueOf(fiscalAmount);
										listAmount.add(fiscalAmount);
									}
								listAmount.add(fiscalAmountTotal.toString());
								loAccountsAllocationBean.setTotal(fiscalAmountTotal.toString());
								listAmount.add(fiscalAmountTotal.toString());
								myMapCOA.put(i, listAmount);
							 }  	
							for(int k=0; k< ((headerArr.size()+1));k++){
								BigDecimal Sum = BigDecimal.ZERO;
								for (Integer key : myMapCOA.keySet()) {   
									//	Integer.parseInt( myMapCOA.get(key).get(k));
										//Sum += Double.parseDouble(myMapCOA.get(key).get(k));
							    		Sum = Sum.add(new BigDecimal(myMapCOA.get(key).get(k)));	      
								} 
						%>
						<td class='alignRht'>
						<span class= "tableData"><%=Sum%></span>
						<%
							}
						%>
					</tr>
					
							<%
								for(int i=0;i<COADetailArr.size();i++)
								{
									loAccountsAllocationBean = (AccountsAllocationBean) COADetailArr.get(i);
									if(i%2==0){
							%>
					<tr class='oddRows'>
							<%
								}
								else
								{
							%>
					<tr>
							<%
								}
							%>
							<td>
							<%if(loAccountsAllocationBean.getChartOfAccount() != ""){ %>
							<%= loAccountsAllocationBean.getChartOfAccount().trim()%><% 
							}
							if(loAccountsAllocationBean.getSubOc() != ""){
							%>-<%=loAccountsAllocationBean.getSubOc()%><%
							}
							if(loAccountsAllocationBean.getRc() != "" ){
							%>-<%=loAccountsAllocationBean.getRc()%>
							<%
							}
							%>
							</td>
							<%
								for(int j=1; j<= headerArr.size();j++){
									String methodName = "fy" + j;
									String fiscalAmount = "";
									fiscalAmount = (String) BeanUtils.getProperty(loAccountsAllocationBean, methodName);	
							%>
							<td class='alignRht'>
							<span class= "tableData"><%=fiscalAmount  %></span>
							</td>
							<%
								}
							%>
							<td class='alignRht'>
							<span class= "tableData"><%=loAccountsAllocationBean.getTotal()%></span>
							</td>
					</tr>
							<%
								}
							%>
								
				</tbody>
			</table>
		</div>

<p>&nbsp;</p>
<h3>Funding Source Allocation</h3>
<%-- Grid Starts --%>
		<div class="tabularWrapper" style="overflow-x:auto">
			 <table cellspacing='0' cellpadding='0' border="1">
				<tbody>
					<tr>
						<th class='center' width='25%'>Funding Sources</th>
						<%
							ArrayList headerArr1 = (ArrayList)request.getAttribute("HeaderList");
							for(int i=0;i<headerArr1.size();i++)
							{
						%>
						<th class='center' width=''>
						<%= (String)headerArr1.get(i) %> 
						</th>
						<%	
							}
						%>
						<th class='center' width=''>Total</th>
					</tr>
					
					<tr>
					
							<td>Overall</td>
							<%
								ArrayList FundingArr = (ArrayList)request.getAttribute("FundingList");
								FundingAllocationBean loFundingAllocationBean = new FundingAllocationBean();
								HashMap<Integer, List<String>> myMap = new HashMap<Integer, List<String>>(); 
								for(int i=0;i<FundingArr.size();i++)
								{
									List<String> listAmount = new ArrayList<String>();
									loFundingAllocationBean = (FundingAllocationBean) FundingArr.get(i);
									BigDecimal fiscalAmountTotal = BigDecimal.ZERO;
									for(int j=1; j<= headerArr1.size();j++){
										String methodName = "fy" + j;
										String fiscalAmount = "";
										
											fiscalAmount = (String) BeanUtils.getProperty(loFundingAllocationBean, methodName);
											fiscalAmountTotal = fiscalAmountTotal.add(new BigDecimal(fiscalAmount));
											//fiscalAmountTotal += Double.valueOf(fiscalAmount);
											listAmount.add(fiscalAmount);
											
										
									}
									listAmount.add(fiscalAmountTotal.toString());
									loFundingAllocationBean.setTotal(fiscalAmountTotal.toString());
									listAmount.add(fiscalAmountTotal.toString());
									myMap.put(i, listAmount);
								 }  	
								List<Integer> Sumabc  = new ArrayList<Integer>();
									for(int k=0; k< ((headerArr1.size()+1));k++){
										BigDecimal Sum = BigDecimal.ZERO;
									for (Integer key : myMap.keySet()) {   
										
										//Integer.parseInt( myMap.get(key).get(k));
										//Sum += Double.parseDouble( myMap.get(key).get(k));
							        	Sum = Sum.add(new BigDecimal(myMap.get(key).get(k)));    
									} 
								%>
								<td class='alignRht'><span class= "tableData"><%= Sum %></span>
								<%
									}
								%>
					</tr>
							
					
					
								<%
								for(int i=0;i<FundingArr.size();i++)
								{
									loFundingAllocationBean = (FundingAllocationBean) FundingArr.get(i);
									if(i%2==0){
								%>
					<tr class='oddRows'>
								<%
									}
									else
									{
								%>
								<tr>
								<%
									}
								%>
							<td>
							<%= loFundingAllocationBean.getFundingSource()%>
							</td>
							<%
								for(int j=1; j<= headerArr1.size();j++){
									String methodName = "fy" + j;
									String fiscalAmount = "";
									fiscalAmount = (String) BeanUtils.getProperty(loFundingAllocationBean, methodName);	
							%>
							<td class='alignRht'>
							<span class= "tableData"><%= fiscalAmount  %></span>
							</td>
							<%
								}
							%>
							<td class='alignRht'>
							<span class= "tableData">
							<%=loFundingAllocationBean.getTotal()%></span>
							</td>
						</tr>
							<%
						 		}
							%>
					
				</tbody>
			</table>
		</div>
<%-- Form Data Ends --%>

<p>&nbsp;</p>


<script type="text/javascript">
$(document)
.ready(
		function() {
			$(".nycgov_header").hide();
			$("#nyc_header_div").hide();
			$(".breadcrumb").hide();
			$(".footer").hide();
			
			$(".tableData").each(function(e) {
				$(this).jqGridCurrency();
			});
			
			$("#ProcurementVal").jqGridCurrency();

		});
</script>
