<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<portlet:defineObjects />
<style> 
	h2{width:82%}
	 table{
		clear: both;
	    width:100%;
	}
	 table .header{
		color: #5077AA;
	    font-size: 13px;
	    font-weight: bold;
	    line-height: 18px;
	    padding: 0 0 10px;
	    font-family: verdana, sans-serif
	}
	 table .questions{
		margin: 0 8px 3px 0;
	    min-height: 20px;
	    padding: 4px 5px 5px 0;
	    text-align: right;
	    width: 49.5%;
	    border-bottom:3px solid #fff;
	    background: #f2f2f2;
	    font-size:12px;
	    font-family: verdana, sans-serif
	}
	 table .answers{
		min-height: 20px;
	    padding: 4px 0 4px 6px;
	    text-align: left;
	    width: 48%;
	    word-break:break-all;
	    font-size:12px;
	    font-family: verdana, sans-serif
	}
	.tableHeaderPrint{
		 background-color: #E4E4E4;
	}
	.tableRowEvenPrint{
		background-color: #FFF
	}
	.tableRowOddPrint{	 	
		background-color: #F2F2F2;
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
	.headingFont{
		font-wight:bold;
		font-size: 18px;
	}
	.sectionHeading{
		font-size: 16px
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
	}
	.subheading{
		font-family: verdana, sans-serif;
		color: #5077AA;
		font-size: 14px;
		font-weight: bold;
		line-height: 18px;
		padding: 6px 0 5px
	}
	h2{
		width: 85%;
	}
	.taskButtons {
		clear: both;
	}
</style>
	<c:if test="${message ne null}">
		<div class="${messageType}" id="messagediv" style="display:block">${message} <img
			src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
			class="message-close" title="Close" alt="Close"
			onclick="showMe('messagediv', this)">
		</div>
	</c:if>
<%=request.getAttribute("openNewWindow")%>
