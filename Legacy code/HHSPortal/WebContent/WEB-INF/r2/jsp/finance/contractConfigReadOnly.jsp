<%@ page errorPage="/error/errorpage.jsp"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@ page import="com.nyc.hhs.util.HHSUtil"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<link rel="stylesheet" media="screen"
	href="${pageContext.servletContext.contextPath}/r2/css/ui.jqgrid.css"
	type="text/css"></link>
<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/grid.locale-en.js"></script>
<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/jquery.jqGrid.min.js"></script>
<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/jquery.formatCurrency-1.4.0.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jqgridDialogOveride.js"></script>
<style>
.boldText {
	font-weight: bold !important;
}
.accContainer{
	width:99.5% !important
}
.accContainer .ui-widget-content{
	border:1px solid #A6C9E2
}
.disabledmultiselect{
   background-color: gray;
}
</style>

<portlet:resourceURL var='SubGridHeaderRow' id='mainAccountGrid'
	escapeXml='false'>
</portlet:resourceURL>

<c:set var="gridColNames">${GridColNames}</c:set>
<c:set var="gridColProp">${MainHeaderProp}</c:set>
<c:set var="subGridColProp">${SubHeaderProp}</c:set>
<portlet:resourceURL var='loadContractConfigReadOnlyGrid'
	id='subAccountGrid' escapeXml='false'>
	<portlet:param name="screenName" value="contractConfigReadOnlyGrid" />
</portlet:resourceURL>


<form id="contractConfigReadOnly" name="contractConfigReadOnly"
	action="<portlet:resourceURL/>" method="post">
	<%
	String lsTransactionMsg = "";
	if (null!=request.getAttribute("transactionMessage")){
		lsTransactionMsg = (String)request.getAttribute("transactionMessage");
	}
	if(null!=request.getAttribute("transactionStatus") && "passed".equalsIgnoreCase((String)request.getAttribute("transactionStatus"))){%>
		<div id="transactionStatusDiv" class="passed" style="display: block"><%=lsTransactionMsg%>
		</div>
	<%}else if(( null != request.getAttribute("transactionStatus") ) && "failed".equalsIgnoreCase((String)request.getAttribute("transactionStatus"))){%>
		<div id="transactionStatusDiv" class="failed" style="display: block"><%=lsTransactionMsg%>
		</div>
	<%}%>

	<h2 class='autoWidth'>Contract Configuration</h2>

	<div class="linkReturnVault alignRht">
		<a	href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=false&app_menu_name=header_maintenance"
		>Return to Contract List</a>
	</div>

	<div class='hr'></div>
	
	<c:set var="loProcurementCOFBean" value="${procurementCOFBean}"></c:set>

	<div class="formcontainer">
		<div class="row">
			<span class="label">Procurement Value:</span> <span class="formfield"><span  id="procVal" >${loProcurementCOFBean.procurementValue} </span></span>
		</div>
		<c:choose>
			<c:when test="${null eq loProcurementCOFBean.compPoolTitle or '' eq loProcurementCOFBean.compPoolTitle}">
				<div class="row">
					<span class="label">Competition Pool:</span> <span class="formfield">N/A</span>
				</div>
			</c:when>
			<c:otherwise>
				<div class="row">
					<span class="label">Competition Pool:</span> <span class="formfield">${loProcurementCOFBean.compPoolTitle}</span>
				</div>
			</c:otherwise>
		</c:choose>
		<div class="row">
			<span class="label">Contract Value:</span> <span class="formfield"><span id="contractVal" >${loProcurementCOFBean.contractValue}</span></span>
		</div>
		<div class="row">
			<span class="label">Contract Start Date:</span> <span
				class="formfield">${loProcurementCOFBean.contractStartDate}</span>
		</div>
		<div class="row">
			<span class="label">Contract End Date:</span> <span class="formfield">${loProcurementCOFBean.contractEndDate}</span>
		</div>
	</div>

	<p>&nbsp;</p>
	<div></div>
	<h3>Chart of Accounts Allocation</h3>
	<div class='accContainer'>
		<div class='gridFormField gridScroll'>
			<jq:grid id="CoAAllocation" gridColNames="${gridColNames}"
				gridColProp="${MainHeaderProp}" subGridColProp="${SubHeaderProp}"
				gridUrl="${SubGridHeaderRow}"
				subGridUrl="${loadContractConfigReadOnlyGrid}" cellUrl="" editUrl=""
				dataType="json" methodType="POST" columnTotalName="${columnsForTotal}"
				isPagination="true" rowsPerPage="5" isSubGrid="true" isReadOnly="true"
				autoWidth="false" isCOAScreen="true"
				operations="del:false,edit:false,add:false,cancel:false,save:false" />
		</div>

	<c:set var="gridColNamesSubBudget"><%=HHSUtil.getHeader("contractBudgetSetUp.grid")%></c:set>
	<c:set var="gridColPropSubBudget"><%=HHSUtil.getHeaderProp("contractBudgetSetUp.grid")%></c:set>
	<c:set var="subGridColPropSubBudget"><%=HHSUtil.getSubGridProp("contractBudgetSetUp.grid")%></c:set>

	<portlet:resourceURL var='SubGridHeaderRowSubBudget'
		id='SubGridHeaderRow' escapeXml='false'>
		<portlet:param name="gridLabel" value="contractBudgetSetUp.grid" />
	</portlet:resourceURL>

	<portlet:resourceURL var='subBudgetGridActions' id='gridOperation'
		escapeXml='false'>
	</portlet:resourceURL>

	<c:forEach var="list" items="${configuredFiscalYrList}">
		<p>&nbsp;</p>
		<portlet:resourceURL var='loadSubBudgetGrid'
			id='loadSubBudgetGridData' escapeXml='false'>
			<portlet:param name="transactionName"
				value="contractBudgetSetUpReadOnlyGrid" />
			<portlet:param name="beanName"
				value="com.nyc.hhs.model.ContractBudgetBean" />
			<portlet:param name="gridLabel" value="contractBudgetSetUp.grid" />
			<portlet:param name="counter" value="${list.budgetfiscalYear}" />
		</portlet:resourceURL>
		<h3>Contract Budgets Setup - FY${fn:substring(list.budgetfiscalYear,2,4)}</h3>
		<div class='gridFormField gridScroll'>
		<jq:grid id="contractBudgetSetup${list.budgetfiscalYear}"
			gridColNames="${gridColNamesSubBudget}"
			gridColProp="${gridColPropSubBudget}"
			subGridColProp="${subGridColPropSubBudget}"
			gridUrl="${SubGridHeaderRowSubBudget}"
			subGridUrl="${loadSubBudgetGrid}" cellUrl="${subBudgetGridActions}"
			editUrl="${subBudgetGridActions}" dataType="json" methodType="POST"
			columnTotalName="" isPagination="true" rowsPerPage="5"
			isSubGrid="true" isReadOnly="true"
			autoWidth="true" 
			operations="del:false,edit:false,add:false,cancel:false,save:false" />
			</div>

		
		<%--R4 Budget Customized  --%>
		<br>
		<B  id="boldBudgetCustomized${list.budgetfiscalYear}">&nbsp;Budget Template</B> 
		<div id="budgetCustomized${list.budgetfiscalYear}">
			<table id="budgetCustomizeTab1${list.budgetfiscalYear}">
			<tr>
				<td><input type="checkbox" id="1${list.budgetfiscalYear}" name="1${list.budgetfiscalYear}" disabled="disabled" />Personnel Services</td>
				<td><input type="checkbox" id="5${list.budgetfiscalYear}" name="5${list.budgetfiscalYear}" disabled="disabled" />Rent</td>
				<td><input type="checkbox" id="9${list.budgetfiscalYear}" name="9${list.budgetfiscalYear}" disabled="disabled" />Unallocated Funds</td>
			</tr>
			<tr>
				<td><input type="checkbox" id="2${list.budgetfiscalYear}" name="2${list.budgetfiscalYear}" disabled="disabled" />Operations and Support</td>
				<td><input type="checkbox" id="6${list.budgetfiscalYear}" name="6${list.budgetfiscalYear}" disabled="disabled" />Contracted Services</td>
				<td><input type="checkbox" id="10${list.budgetfiscalYear}" name="10${list.budgetfiscalYear}" disabled="disabled" />Indirect Rate</td>
			</tr>
			<tr>
				<td><input type="checkbox" id="3${list.budgetfiscalYear}" name="3${list.budgetfiscalYear}" disabled="disabled" />Utilities</td>
				<td><input type="checkbox" id="7${list.budgetfiscalYear}" name="7${list.budgetfiscalYear}" disabled="disabled" />Rate</td>
				<td><input type="checkbox" id="11${list.budgetfiscalYear}" name="11${list.budgetfiscalYear}" disabled="disabled" />Program Income</td>
			</tr>
			<tr>
				<td><input type="checkbox" id="4${list.budgetfiscalYear}" name="4${list.budgetfiscalYear}" disabled="disabled" />Professional Services</td>
				<td><input type="checkbox" id="8${list.budgetfiscalYear}" name="8${list.budgetfiscalYear}" disabled="disabled" />Milestone</td>
				<td></td>
			</tr>
		</table>
		</div>

	
<div>&nbsp;</div>
				<!-- Start: Added service checkbox in R7  -->
<c:set var="displayServices" value="true"></c:set>
<c:if test="${(costCenterOpted eq 0)}">
<c:set var="displayServices" value="false"></c:set>
</c:if>
<c:if test="${displayServices}">
<h3>Services</h3>
<div id="servicesCustomizeTab2">
<input type="checkbox" id="servCheckbx" disabled="disabled" name="servCheckbx"
<c:if test="${(costCenterOpted eq 2)}">checked="checked"</c:if>/>Enable Services for this contract

</div>
<br>
<c:if test="${(costCenterOpted eq 2)}">
<div id="servicesListTab">
		<div class="userAccessOverlay" id="userAccessleft" style="height:250px;width:300px;float: left;margin:0%;padding-bottom: 1%;">
						<p class="addInfouserAccess boldtextDefaultHeader" style="background-color:#ccc;text-align:center;border:1px solid #ccc;">
							Available Services
						</p>
						
		<div class="multiselect disabledmultiselect" id="disabledService" >
            <ul style="padding-left:9%;">
            <c:forEach items="${list.servicesBudgetDetails}" var="entry">
								<c:if test="${entry.key eq 'enabledServices'}">
								<c:forEach var="userAccessListVar" items="${entry.value}"
									varStatus="loop">
								<li id="${userAccessListVar.costCenterServiceMappingId}"><span title="${userAccessListVar.enabledServiceName}">${userAccessListVar.enabledServiceName}</span></li>			
								</c:forEach>
								</c:if>
								</c:forEach>
            </ul>
        </div>
					</div>
						
					<div class="userAccessOverlay" id="userAccessRight" style="height:250px;width:300px;float: right;margin:0%;padding: 0px;margin-top: 0%;margin-right: 30%">
						<p class="boldtextDefaultHeader" style="background-color:#ccc;text-align:center;border:1px solid #ccc;">
							Enabled Services
						</p>

		<div class="multiselect disabledmultiselect" id="enabledService" >
        	    <ul style="padding-left:9%;">
								<c:forEach items="${list.servicesBudgetDetails}" var="entry">
								<c:if test="${entry.key eq 'selectedServices'}">
								<c:forEach var="userAccessListVar" items="${entry.value}"
									varStatus="loop">
							<li id="${userAccessListVar.costCenterServiceMappingId}"><span title="${userAccessListVar.enabledServiceName}">${userAccessListVar.enabledServiceName}</span></li>			
						</c:forEach>
					</c:if>
				</c:forEach>
						</ul>
						</div>
					</div>	
	</div>
	</c:if>
	</c:if>
</c:forEach>
</div>
</form>
<style type="text/css">
input[disabled] {
    background-color: #d2d2d2 !important;
    color: #999 !important;
    border: 2px solid #d2d2d3 !important;
    cursor: default !important;
    }
     div[disabled], .disabledmultiselect{
    background-color: #d2d2d2 !important;
    color: #999 !important;
    cursor: default !important;
    }
            .multiselect{
                width:100%;
                height:92%;
                overflow: scroll;
                background-color:aliceblue;
                
            }
            .multiselect li {
               
                padding-left: 0em; 
                text-indent: -2em;
            }
            .multiselect ul{
                list-style-type: none;
                white-space: nowrap ;
            }
            .disabledmultiselect{
                background-color: #d2d2d2 !important;
            }
        
</style>
<script type="text/javascript">
						<!-- End: Added service checkbox in R7  -->

$(document)
.ready(
		function() {
            
			$("#procVal").jqGridCurrency();
			$("#contractVal").jqGridCurrency();
			//START - R4 Budget Customized 
			var entryTypeData = '${EntryTypeList}';
			entryTypeData = entryTypeData.split('||');
			if(entryTypeData != ''){
				for(var i=0; i<entryTypeData.length; i++){
					var _tab = $.trim(entryTypeData[i]);
					var _fy = _tab.substring(4,0);
					_tab = _tab.replace(_tab.substring(4,0),'').replace('_[','').replace(']','').split(',');
					if(navigator.appName == "Microsoft Internet Explorer"){
						$('#boldBudgetCustomized'+_fy).prepend('<br>');
					}
					// Update for R2/R3 checkBox checked
					if($.trim(_tab) != '')
						for(var j=0; j<_tab.length; j++)
							$('#'+$.trim(_tab[j]).split(':')[0]+_fy).prop('checked', true);
					else
						for(var j=1; j<12; j++)
							$('#'+j+_fy).prop('checked', true);
				}
			}
			//END - R4 Budget Customized 
		});
</script>
