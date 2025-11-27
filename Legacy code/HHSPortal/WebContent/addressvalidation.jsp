<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="java.util.List, java.util.Iterator, com.nyc.hhs.model.AddressBean" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="../framework/skeletons/hhsa/js/address_validation.js"></script>
<form name="myform" action="<portlet:actionURL/>" method ="post" >
	<p><%=request.getAttribute("description")%></p>
	<div class='tabularWrapper'>
		<table id='addresstable' name="addresstable" onclick="applyCssToTable('addresstable')">
			<tr class="heading">
				<td>Action</td>
				<td>Address</td>
				<td>Type</td>
			</tr>
			<% List<AddressBean> addressList = (List<AddressBean>)request.getAttribute("addressList");
			if(null != addressList){
				for(int i = 0; i < addressList.size(); i++){
					String checked = "";
					AddressBean addressBean = addressList.get(i);
					if(i == 0){
						checked = "checked";
					}
			%>
			<tr>
				<td><input type="radio" id="address" name="address" value="<%=addressBean.getMsAddressType()%>" class='rdoBtn' <%=checked%>></input></td>
					<td><%=addressBean.getMsAddress1()%><br/><%=addressBean.getMsCity()+", "+addressBean.getMsState()+" "+addressBean.getMsZipcode()%>
					<input type="hidden" name="newAddress" value="<%=addressBean.getMsAddress1()%>"/>
					<input type="hidden" name="newCity" value="<%=addressBean.getMsCity()%>"/>
					<input type="hidden" name="newState" value="<%=addressBean.getMsState()%>"/>
					<input type="hidden" name="newZip" value="<%=addressBean.getMsZipcode()%>"/>
					<input type="hidden" name="StatusDescriptionText"  value="<%=addressBean.getMsStatusDescriptionText()%>"/>
					<input type="hidden" name="StatusReason"  value="<%=addressBean.getMsStatusReason()%>"/>
					<input type="hidden" name="StreetNumberText"   value="<%=addressBean.getMsStreetNumberText()%>"/>
					<input type="hidden" name="CongressionalDistrictName"   value="<%=addressBean.getMsCongressionalDistrictName()%>"/>
					<input type="hidden" name="Latitude"  value="<%=addressBean.getMsLatitude()%>"/>
					<input type="hidden" name="Longitude"  value="<%=addressBean.getMsLongitude()%>"/>
					<input type="hidden" name="XCoordinate"  value="<%=addressBean.getMsXCoordinate()%>"/>
					<input type="hidden" name="YCoordinate"  value="<%=addressBean.getMsYCoordinate()%>"/>
					<input type="hidden" name="CommunityDistrict"  value="<%=addressBean.getMsCommunityDistrict()%>"/>
					<input type="hidden" name="CivilCourtDistrict"  value="<%=addressBean.getMsCivilCourtDistrict()%>"/>
					<input type="hidden" name="SchoolDistrictName"  value="<%=addressBean.getMsSchoolDistrictName()%>"/>
					<input type="hidden" name="HealthArea"  value="<%=addressBean.getMsHealthArea()%>"/>
					<input type="hidden" name="BuildingIdNumber"  value="<%=addressBean.getMsBuildingIdNumber()%>"/>
					<input type="hidden" name="TaxBlock"  value="<%=addressBean.getMsTaxBlock()%>"/>
					<input type="hidden" name="TaxLot"  value="<%=addressBean.getMsTaxLot()%>"/>
					<input type="hidden" name="SenatorialDistrict"  value="<%=addressBean.getMsSenatorialDistrict()%>"/>
					<input type="hidden" name="AssemblyDistrict"  value="<%=addressBean.getMsAssemblyDistrict()%>"/>
					<input type="hidden" name="CouncilDistrict"  value="<%=addressBean.getMsCouncilDistrict()%>"/>
					<input type="hidden" name="LowEndStreetNumber"  value="<%=addressBean.getMsLowEndStreetNumber()%>"/>
					<input type="hidden" name="HighEndStreetNumber"  value="<%=addressBean.getMsHighEndStreetNumber()%>"/>
					<input type="hidden" name="LowEndStreetName"  value="<%=addressBean.getMsLowEndStreetName()%>"/>
					<input type="hidden" name="HighEndStreetName"  value="<%=addressBean.getMsHighEndStreetName()%>"/>
					<input type="hidden" name="NYCBorough"  value="<%=addressBean.getMsNycBorough()%>"/>
				</td>
				<td><%=addressBean.getMsAddressType()%></td>
			</tr>
			<%}} %>
		</table>
	</div>
	<div class="buttonholder">
		<input type="button" value="Cancel" title="Cancel" id="canceladdrvalidation" class="graybtutton"/>
		<c:choose>
			<c:when test="${addressUnavailable}">
				<input type="button" class="button" value="OK" title="OK" id="selectaddress">
			</c:when>
			<c:otherwise>
				<input type="button" class="button" value="Select" title="Select" id="selectaddress">
			</c:otherwise>
		</c:choose>
	</div>
</form>