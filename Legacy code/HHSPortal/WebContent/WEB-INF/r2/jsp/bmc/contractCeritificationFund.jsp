<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<portlet:defineObjects />
<link rel="stylesheet" media="screen" href="${pageContext.servletContext.contextPath}/r2/css/ui.jqgrid.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/grid.locale-en.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.formatCurrency-1.4.0.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jqgridDialogOveride.js"></script>
<script type="text/javascript" src="../resources/js/autoNumeric-1.7.5.js"></script>

<portlet:actionURL var="addProcurementUrl" escapeXml="false">
<portlet:param name="submitAction" value="addProcurement"/>
</portlet:actionURL>
<portlet:resourceURL var='mainAccountGrid' id='mainAccountGrid' escapeXml='false'>
<portlet:param name="screenName" value="contractCofAccountGrid"/>
</portlet:resourceURL>
<portlet:resourceURL var='subAccountGrid' id='subAccountGrid' escapeXml='false'>
<portlet:param name="screenName" value="contractCofAccountGrid"/>
</portlet:resourceURL>
<portlet:resourceURL var='accountOperationGrid' id='accountOperationGrid' escapeXml='false'>
<portlet:param name="screenName" value="contractCofAccountGrid"/>
</portlet:resourceURL>

<portlet:resourceURL var='mainFundingGrid' id='mainFundingGrid' escapeXml='false'>
<portlet:param name="screenName" value="contractCofFundingGrid"/>
</portlet:resourceURL>
<portlet:resourceURL var='subFundingGrid' id='subFundingGrid' escapeXml='false'>
<portlet:param name="screenName" value="contractCofFundingGrid"/>
</portlet:resourceURL>
<portlet:resourceURL var='fundingOperationGrid' id='fundingOperationGrid' escapeXml='false'>
<portlet:param name="screenName" value="contractCofFundingGrid"/>
</portlet:resourceURL>
<portlet:resourceURL var='columnsForTotal' id='columnsForTotal' escapeXml='false'>
</portlet:resourceURL>


<form:form id="myform" name="myform" action="${addProcurementUrl}" method ="post" >
<DIV id=tabs-container><H2 class='autoWidth'>Contract Certification of Funds Details</H2>
<div class=hr></div>
<DIV class=formcontainer><div class=row><SPAN class='clearLabel'>Agency:</SPAN> <SPAN class=formfield>Department of Homeless Services</SPAN> </div>
<DIV class=row><SPAN class=clearLabel>Agency Code:</SPAN> <SPAN class=formfield>000</SPAN> </DIV>
<DIV class=row><SPAN class=clearLabel>Contract Value:</SPAN> <SPAN class=formfield>$0,000,000.00</SPAN> </DIV>
<DIV class=row><SPAN class=clearLabel>Procurement Title:</SPAN> <SPAN class=formfield>Health Services for Low Income Families</SPAN> </DIV>
<DIV class=row><SPAN class=clearLabel>Contract Start Date:</SPAN> <SPAN class=formfield>08/01/2012</SPAN> </DIV>
<DIV class=row><SPAN class=clearLabel>Contract End Date:</SPAN> <SPAN class=formfield>08/01/2012</SPAN> </DIV>
<DIV class=row><SPAN class=clearLabel>Provider:</SPAN> <SPAN class=formfield>xyz</SPAN> </DIV>
<DIV>
<P>&nbsp;</P>
<H3>Chart of Accounts Allocation</H3>
<DIV>
<c:set var="ab" value="true"></c:set>

<jq:grid id="account" 
         gridColNames="${GridColNames}" 
	     gridColProp="${MainHeaderProp}"
		 gridUrl="${mainAccountGrid}"
		 subGridUrl="${subAccountGrid}"
	     cellUrl="${accountOperationGrid}"
	     editUrl="${accountOperationGrid}"
	     dataType="json" methodType="POST"
	     subGridColProp="${SubHeaderProp}"
         columnTotalName="${columnsForTotal}"
	     rowsPerPage="5"
         isSubGrid="true"
         isPagination="true"
	     operations="del:true,edit:true,add:true,cancel:true,save:true"
/>

</DIV>
<P>&nbsp;</P>
<H3>Funding Source Allocation (Optional)</H3>
<DIV><b>The optional fields below may be used to indicate the funding source allocation at the point of the initial Certification of Funds. These fields are for reference purposes only.</b></DIV>
<DIV><jq:grid id="funding" 
         gridColNames="${FundingGridColNames}" 
	     gridColProp="${FundingMainHeaderProp}"
		 gridUrl="${mainFundingGrid}"
		 subGridUrl="${subFundingGrid}"
	     cellUrl="${fundingOperationGrid}"
	     editUrl="${fundingOperationGrid}"
	     dataType="json" methodType="POST"
	     subGridColProp="${FundingSubHeaderProp}"
         columnTotalName="${columnsForTotal}"
         isPagination="false"
	     rowsPerPage="4"
         isSubGrid="true"
	     operations="del:false,edit:true,add:false,cancel:true,save:true"
/></DIV><%-- Horizontal Row starts --%><%-- Form Data Ends --%>
<DIV class=buttonholder><INPUT class=button value="Submit CoF" type=button></DIV><%-- Form Data Ends --%></DIV></DIV></DIV>
</form:form>