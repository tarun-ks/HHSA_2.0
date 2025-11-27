<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils;" %>
<portlet:defineObjects/>
<!-- Document Vault Search -->
<form id="searchform" action="<portlet:actionURL/>" method ="post" name='searchform'>
<input type="hidden" name="selectedOrgTypeForLinkage" id ="selectedOrgTypeForLinkage" value="${selectedOrgTypeForLinkage}"/>
	<div id="searchDoc" class="searchDoc" style="display: none">
	<div class="searchformcontainer">
	<div id="leftFields">
	<input type="hidden" value="" name="normalSearchOrgType" id="normalSearchOrgType"/>
	<input type="hidden" value="" name="clickedSearch" id="clickedSearch"/>
 	<input type="hidden" value="" name="normalSearchOrgId" id="normalSearchOrgId"/>
	 <div class="fieldheading">Folder/Document Information</div>
	   <div class='row'>
	   <span class="label">Folder/Document Name:</span> 
	   <span class='formfield'>
		<input type="text" id="docName" name="docName" maxlength="50" class="input groupName" onchange="eraseError()"/>
		</span><br/>
		<span class="error"></span>
		</div><br>
		<div class='row'>
		 <span class='label'>Document Type:</span>
		 <span class='formfield'>
		  <table cellspacing="0" cellpadding="0" border="0" class="ddcombo_table" id="combotable1" style='position:relative;right:5px;'><tbody>
								<tr>
									<td class="ddcombo_td1 ddcombo_td1-align">
										<div class="ddcombo_div4" style="background: url(&quot;../framework/skins/hhsa/images/transparent_pixel.gif&quot;) repeat scroll 0% 0% transparent;">
											<input type="text" path="filtertype" value="" name="doctype_city" id="filtertype" class="input groupName" style='width:233px;' onkeypress="if (this.value.length > 60) { return false; }" />
											<input type="hidden" name="doctype_city" id ="filtertype"/>
										<span class="error"></span>
										</div>
									</td>
									<td valign="top" align="left" class="ddcombo_td2" id="combotable_button1"><a></a><img src="../framework/skins/hhsa/images/button2.png" style="display: none;"></td>
								</tr></tbody>
							</table>
							</span>
							<span class="error"></span>
							</div>
					<div style="display:none;margin-left:198px;margin-right:22px;border: 1px solid black;
					background-color: white;
					overflow: hidden;
					z-index: 99999;position: absolute;width: 257px;" id="optionsBox1">
							<ol id= "dropdownul1" style="max-height: 180px; overflow: auto;">
								<c:forEach items="${docTypedropDownList1}" var="entry">
							        <li class="ddcombo_event data">${entry}</li>
							    </c:forEach>
							</ol>
							<span class="error docTypeError"></span>
						</div><br>
			<div class="fieldheading">Sharing Information</div>
			<div class='row'>
					<span class='label'>Shared:</span>
					 <select id="shared" name="shared" class="input groupName" onchange="disableShareWith(this)">
					    <option value=""></option>
						<option value="shared">Only Shared</option>
						<option value="unshared">Only Un-shared</option>
					</select>
					
			</div><br>
			<div class="fieldheading">Document Linkages</div>
			 <c:if test="${org_type eq 'agency_org'}">
				<div class='row'>
					<span class='label'>Document Linked to: </span>
					 <select id="linked" name="linked" class="input groupName" onchange="linkDocTo()" >
						<option value=""></option>
						<option value="amendment">Amendment</option>
						<option value="agencyAward">Agency Award</option>
						<option value="Contract">Contract</option>
						<option value="providerAward">Provider Award</option>
						<!-- Added for Release 6: Returned Payment linked entity search -->
						<option value="returnedPayment">Returned Payment</option>
						<!-- Added for Release 6: Returned Payment linked entity search end-->
					</select>
				</div>
			</c:if>
		       <c:if test="${org_type eq 'provider_org'}">
				<div class='row'>
					<span class='label'>Document Linked to: </span>
					 <select id="linked" name="linked" class="input groupName" onchange="linkDocTo()" >
						<option value=""></option>
						<option value="Award">Award</option>
						<option value="Budget">Budget</option>
						<option value="Business Application">Business Application</option>
						<option value="Filings">Filings</option>
						<option value="Invoice">Invoice</option>
						<option value="Proposal">Proposal</option>
						<option value="Service Application">Service Application</option>
					</select>
		       </div>
		       </c:if>
		        <c:if test="${org_type eq 'city_org'}">
		       <div class='row'>
					<span class='label'>Document Linked to: </span>
					 <select id="linked" name="linked" class="input groupName" onchange="linkDocTo()" >
						<option value=""></option>
						<option value="Procurement">Procurement</option>
						</select>
				</div>
		      </c:if>
	 </div><br>
	<div id="rightFields">
				<div class='row'>
					<span class='label'>Modified Date Range:</span> 
					<span class='formfield modifieddaterange' style='width:57% !important'>
					<span>
					<input type="text" name="submittedfrom" size="10" id='submittedfrom' value="" maxlength="10" validate="calender" class="groupName" />
					<img title="Modified From Date" alt="Modified From Date" src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('submittedfrom',event,'mmddyyyy');return false;" />
					/ <input type="text" name="submittedTo" size="10"
						id='submittedTo' class="groupName" value="" maxlength="10" validate="calender" /> 
						<img title="Modified To Date" alt="Modified To Date"
						src="../framework/skins/hhsa/images/calender.png"
						onclick="NewCssCal('submittedTo',event,'mmddyyyy');return false;" />
						</span></br>
						<span class="error" for="submittedfrom" style='width:62% !important;float: right;'></span>
						<span class="error" for="submittedTo" style='width:62% !important;float: right;' ></span>
					</span>
				</div>
				<div id='addsharedmargin'></div>
				<div class='row' id="sharedwithdiv">
					<span class='label'>Shared with:</span> 
							<span class='formfield'><input type="text" size="32" name="sharedWith" id="sharedWith" class="groupName" disabled="true"/></span>
						<input type="hidden" name="sharedWithValue" id ="sharedWithValue">
						<span class="error" id="sharedWitherror" style="float: right;"></span>
				</div><br><br>
				<div id="serchLinkedFieldsContainer">
				<div class='row' id="procTitleDiv" style="display: none;">
					<span class='label'>Procurement Title:</span>				
					<span class='formfield'></span>
		  <input type="text" size="32" path="procurementTitle" name="procurementTitle" id="procurementTitle" class="input" />
		  <input type="hidden" name="procurementTitle" id ="procurementTitle">
		  <input type="hidden" name="procurementId" id ="procurementId">
		  <span class="error" style="float:left;"></span>
				</div>
					<div class='row' id="awardepindiv" style="display: none;">
					<span class='label'>Award E-PIN:</span> 
					<span class='formfield'></span>
		  		<input type="text" size="32" path="awardepinTitle" name="awardepinTitle" id="awardepinTitle" class="input" />
		  		<input type="hidden" name="awardepinTitle" id ="awardepinTitle">
		  		<span class="error" style="float:left;"></span>
				</div>
				<div class='row' id="contractAwardEpinDiv" style="display: none;">
					<span class='label'>Award E-PIN:</span> 
					<span class='formfield'></span>
		  		<input type="text" size="32" path="contractawardepinTitle" name="contractawardepinTitle" id="contractawardepinTitle" class="input" />
		  		<input type="hidden" name="contractawardepinTitle" id ="contractawardepinTitle">
		  		<span class="error" style="float:left;"></span>
				</div>
				<div class='row' id="amendmentepindiv" style="display: none;">
				<span class='label'>Amendment E-PIN:</span> 
				<span class='formfield'></span>
		  		<input type="text" size="32" path="amendmentepinTitle" name="amendmentepinTitle" id="amendmentepinTitle" class="input" />
		  		<input type="hidden" name="amendmentepinTitle" id ="amendmentepinTitle">
		  		<span class="error" style="float:left;" ></span>
				</div>
				<div class='row' id="paymentNumber" style="display: none;">
					<span class='label'>Payment Number :</span> 
					<input type="text" size="32" name="paymentnoTitle" id="paymentnoTitle" />
					<span class="error"></span>
				</div>    
				<div class='row' id="invoiceDiv" style="display: none;">
					<span class='label'>Invoice Number:</span> 
					<input type="text" size="32" name="invoiceNum" id="invoiceNum"/>
					<span class="error" style="float:left;"></span>
				</div>
				<div class='row' id="cTNumDiv" style="display: none;">
					<span class='label'>CT Number:</span>
				    <input type="text" size="32" name="cTNum" id="cTNum"/>
				    <span class="error"></span>
				</div>
				<div class='row' id="submitDateRangeFrom" style="display: none;">
					<span class='label'>Submitted Date Range:</span>
					<span class='formfield modifieddaterange'  style='width:44% !important'>
					<span>
					 <input type="text"
						name="submittedfrom1" id='submittedfrom1' class="groupName" style="width: 85px;"
						value="" maxlength="10" validate="calender" /> <img
						title="Modified From Date" alt="Modified From Date"
						src="../framework/skins/hhsa/images/calender.png"
						onclick="NewCssCal('submittedfrom1',event,'mmddyyyy');return false;" />/
					 <input type="text" name="submittedTo1" style="width: 85px;"
						id='submittedTo1' class="groupName" value="" maxlength="10" validate="calender" /> <img
						title="Modified To Date" alt="Modified To Date"
						src="../framework/skins/hhsa/images/calender.png"
						onclick="NewCssCal('submittedTo1',event,'mmddyyyy');return false;" />
						<span class="error" for="submittedfrom1" style='width:62% !important;float: right;'></span>
						<span class="error" for="submittedTo1" style='width:62% !important;float: right;' ></span>
						</span></br>
						</span>
				</div> 
				</div>
	
	</div>
	<div class="buttonholder">
				<input type="button" id="clearfilter" title="Reset all the search criteria fields" value="Clear Fields" onclick="clearFilter()" class="graybtutton" />
				<input type="submit" value="Search" title="Search" id="filter"/>
			</div>			
	 <div style="clear: both"></div>
	</div>
	</div>
</form>
<!-- End -->

<!-- RecycleBin Search -->
<form id="deleteform" action="<portlet:actionURL/>" method ="post" name='deleteform'>
<div id="recyclebindiv" style="display: none;">
	<div class="searchformcontainer">
	<div id="leftFields">
	<!-- Emergency Build- 4.0.1- changes for recycle bin message -->
		<input type="hidden" value="searchMessageFlag" name="searchMessageFlag" id="searchMessageFlag"/>
		<!-- Emergency Build- 4.0.1- changes for recycle bin message -->
	<div class="fieldheading">Folder/Document Information</div>
			<div class='row'>
				<span class="label">Folder/Document Name:</span> 
				<span class='formfield'  style='width:44% !important'>
				<input type="text" id="docNameRec" name="docName" maxlength="50" class="input groupName"/></span></br>
				<span class="error"></span>
			</div><br>
            <div class='row'>
				<span class="label">Document Type:</span> 
				<span class='formfield'>
	  				<table cellspacing="0" cellpadding="0" border="0" class="ddcombo_table" id="combotableForRecycleBin" style='position:relative;right:5px;'><tbody>
							<tr>
								<td class="ddcombo_td1 ddcombo_td1-align">
									<div class="ddcombo_div4" style="background: url(&quot;../framework/skins/hhsa/images/transparent_pixel.gif&quot;) repeat scroll 0% 0% transparent;">
										<input type="text" path="docTypeRec" value="" name="docTypeRec" id="docTypeRec" class="input groupName" style='width:233px;' onkeypress="if (this.value.length > 60) { return false; }" />
										<input type="hidden" name="docTypeRec" id ="docTypeRec"/>
									<span class="error"></span>
									</div>
								</td>
							<td valign="top" align="left" class="ddcombo_td2" id="combotable_buttonForRecycleBin"><a></a><img src="../framework/skins/hhsa/images/button2.png" style="display: none;"></td>	
							</tr></tbody>
						</table>
						</span>
						<span class="error"></span>
			</div>
			    <div style="display:none;margin-left:198px;margin-right:22px;border: 1px solid black;
				background-color: white;
				overflow: hidden;
				z-index: 99999;position: absolute;width: 257px;" id="optionsBoxForRecycleBin">
						<ol id= "dropdownulForRecycleBin" style="max-height: 180px; overflow: auto;">
							<c:forEach items="${docTypedropDownList1}" var="entry">
						        <li class="ddcombo_event data">${entry}</li>
						    </c:forEach>
						</ol>
						<span class="error docTypeError"></span>
			   </div>
		</div>
   <div id="rightFields"><br>
			<div class='row'>
				<span class="label">Deletion Date Range:</span> 
				<span class='formfield modifieddaterange'  style='width:44% !important'>
				<span>
				<input type="text" name="modifiedfrom2" class="groupName"
					id='modifiedfrom2' value="" size="10" maxlength="10" validate="calender" /> <img
					title="Modified From Date" alt="Modified From Date"
					src="../framework/skins/hhsa/images/calender.png"
					onclick="NewCssCal('modifiedfrom2',event,'mmddyyyy');return false;" />/
				<input type="text" name="modifiedto2" class="groupName" size="10" id='modifiedto2' value="" maxlength="10" validate="calender" /> <img
					title="Modified To Date" alt="Modified To Date"
					src="../framework/skins/hhsa/images/calender.png"
					onclick="NewCssCal('modifiedto2',event,'mmddyyyy');return false;" /></span></br>
					<span class="error" for="modifiedfrom2" style='width:62% !important;float: right;'></span>
					<span class="error" for="modifiedto2" style='width:62% !important;float: right;' ></span>
					</span>
			</div>
		</div>

		<div class="buttonholder">
			<input type="button" id="clearfilter1" title="Reset all the search criteria fields" value="Clear Fields" onclick="clearFilter()" class="graybtutton" />
			<!-- Changed title-Defect # 7251-->
			<input type="submit" value="Search" title="Search" id="filter1"/>
		</div>	
	<div style="clear: both"></div>
	</div>

</div>

</form>
<!-- End -->
<!-- Search Org Documents  -->
<c:if test="${org_type eq 'city_org'}">
<form id="findorgdocform" action="<portlet:actionURL/>" method ="post" name="findorgdocform">
	<div id="findDoc" style="display:none;">
		<div class="searchformcontainer">
			<div id="leftFields">
			<input type="hidden" value="searchMessageFlag" name="searchMessageFlag" id="searchMessageFlag"/>
				<div class="fieldheading">Document Information</div>
				<div class='row'>
					<span class="label">Document Name:</span> 
					<input type="text" size="32" id="docName" maxlength="50" name="docName" value ="${docName}"/></span></br>
					<span class="error"></span>
				</div><br>
				<div class='row'>
					<span class="label"><label class='required'>&#42;</label>Document Type:</span>
					<span class='formfield'  style='width:44% !important'>
						<table cellspacing="0" cellpadding="0" border="0" class="ddcombo_table" id="combotable2" style='position:relative;right:5px;'>
							<tbody>
								<tr>
									<td class="ddcombo_td1 ddcombo_td1-align">
										<div class="ddcombo_div4" style="background: url(&quot;../framework/skins/hhsa/images/transparent_pixel.gif&quot;) repeat scroll 0% 0% transparent;">
											<input type="text" path="doctype_city" value="" name="doctype_city" id="doctype_city" style='width:222px;' class="input groupName" />
											<input type="hidden" name="doctype_city" id ="doctype_city" value="${doctype_city}"></span>
											<span class="error"></span>
										</div>
									</td>
									<td valign="top" align="left" class="ddcombo_td2" id="combotable_button2"><a></a><img src="../framework/skins/hhsa/images/button2.png" style="display: none;"></td>
								</tr>
							</tbody>
						</table>
						<div style="display:none;margin-left:198px;margin-right:22px;border: 1px solid black;
							background-color: white;
							overflow: hidden;position: absolute;width: 257px;
							z-index: 99999;" id="optionsBox2">
							<ol id= "dropdownul2" style="max-height: 180px; overflow: auto;">
								<c:forEach items="${docTypedropDownListOtherOrg}" var="entry">
							        <li class="ddcombo_event data">${entry}</li>
							    </c:forEach>
							</ol>
							<span class="error docTypeError"></span>
						</div>
					</span></br>
				</div>
               <div class='row'>
					<span class="label">Modified Date Range:</span> 
					<span class='formfield modifieddaterange'  style='width:44% !important'>
					<span>
					<input type="text" size="10" name="modifiedfrom5" id='modifiedfrom5' value=""
						maxlength="10" validate="calender" /> <img
						title="Modified From Date" alt="Modified From Date"
						src="../framework/skins/hhsa/images/calender.png"
						onclick="NewCssCal('modifiedfrom5',event,'mmddyyyy');return false;" />
					/<input type="text" size="10" name="modifiedto5" id='modifiedto5' value=""
						maxlength="10" validate="calender" /> <img
						title="Modified To Date" alt="Modified To Date"
						src="../framework/skins/hhsa/images/calender.png"
						onclick="NewCssCal('modifiedto5',event,'mmddyyyy');return false;" /></span></br>
						<span class="error" for="modifiedfrom5" style='width:62% !important;float: right;'></span>
						<span class="error" for="modifiedto5" style='width:62% !important;float: right;' ></span>
						</span>
				</div>
					<p style="font-size: x-small;"><label class='required'>&#42;</label>Indicates required fields</p>
			</div><br>
			<div id="rightFields">
				<div class="row">
					<span class="label" style="height:105px;">Shared With:</span>
					<div class="checboxcontainer">
			   			<c:forEach items="${portletSessionScope.agencyList}" var="agencyName">
			    			<input type="checkbox" name="agencyCheckBox" value="${agencyName.hiddenValue}">${agencyName.displayValue}<br/> 
						</c:forEach>
					</div>
				</div>
			</div>
		</div>
		<div class="buttonholder">
			<input type="button" id="clearfilter2" title="Reset all the search criteria fields" value="Clear Fields" onclick="clearFilter()" class="graybtutton" />
			<input type="submit" value="Search" title="Search" id="filter3"/>
		</div>	
		<div style="clear: both"></div>
	</div>
</form>
</c:if> 
<c:if test="${org_type eq 'agency_org' || org_type eq 'provider_org'}">
 <form id="findshareddocform" action="<portlet:actionURL/>" method ="post" name='findshareddocform'>
 <div id="findDoc" style="display:none;">
 <div class="searchformcontainer">
 <div id="leftFields">
 	<input type="hidden" value="" name="sharedSearchOrgType" id="sharedSearchOrgType"/>
 	<input type="hidden" value="" name="sharedSearchOrgId" id="sharedSearchOrgId"/>
 	<input type="hidden" value="searchMessageFlag" name="searchMessageFlag" id="searchMessageFlag"/>
	  <div class="fieldheading">Document Information</div>
		<div class='row'>
					<span class="label">Document Name:</span> 
					<input type="text" name="docName" maxlength="50" id="docName" class="input groupName"/><br>
					<span class="error"></span>
		</div><br><br><br><br>
              <div class='row'>
					<span class="label">Document Type:</span> 
					<span class='formfield'  style='width:44% !important'>
											  <table cellspacing="0" cellpadding="0" border="0" class="ddcombo_table" id="combotable2" style='position:relative;right:5px;'><tbody>
							<tr>
								<td class="ddcombo_td1 ddcombo_td1-align">
									<div class="ddcombo_div4" style="background: url(&quot;../framework/skins/hhsa/images/transparent_pixel.gif&quot;) repeat scroll 0% 0% transparent;">
										<input type="text" path="doctype_city" value="" name="doctype_city" id="doctype_city" style='width:233px;' class="input groupName" onkeypress="if (this.value.length > 60) { return false; }" />
										<input type="hidden" name="doctype_city" id ="doctype_city"></span>
									<span class="error"></span>
									</div>
								</td>
								<td valign="top" align="left" class="ddcombo_td2" id="combotable_button2"><a></a><img src="../framework/skins/hhsa/images/button2.png" style="display: none;"></td>
							</tr></tbody>
						</table>
						</span>
						</div>
<div style="display:none;margin-left:198px;margin-right:22px;border: 1px solid black;
				background-color: white;
				overflow: hidden;
				z-index: 99999;position: absolute;width: 257px;" id="optionsBox2">
						<ol id= "dropdownul2" style="max-height: 180px; overflow: auto;">
							<c:forEach items="${docTypedropDownListOtherOrg}" var="entry">
						        <li class="ddcombo_event data">${entry}</li>
						    </c:forEach>
						</ol>
						<span class="error docTypeError"></span>
					</div>
					</span></br>
					
				</div><br>
		<div id="rightFields">
			<div class="row">
			<span class="label">Modified Date Range:</span> 
					<span class='formfield modifieddaterange'  style='width:54% !important'>
					<span>
					<input type="text" size="10" name="modifiedfrom5" id='modifiedfrom5' class="groupName" value=""
						maxlength="10" validate="calender" /> <img
						title="Modified From Date" alt="Modified From Date"
						src="../framework/skins/hhsa/images/calender.png"
						onclick="NewCssCal('modifiedfrom5',event,'mmddyyyy');return false;" />
					/<input type="text" size="10" name="modifiedto5" id='modifiedto5' class="groupName" value=""
						maxlength="10" validate="calender" /> <img
						title="Modified To Date" alt="Modified To Date"
						src="../framework/skins/hhsa/images/calender.png"
						onclick="NewCssCal('modifiedto5',event,'mmddyyyy');return false;" /></span><br>
					    <span class="error" for="modifiedfrom5" style='width:62% !important;float: right;'></span>
						<span class="error" for="modifiedto5" style='width:62% !important;float: right;' ></span>
					    </span>	
			</div><br><br><br><br>
			<div class="row">
			<span class="label">Shared Date Range:</span> 
					<span class='formfield modifieddaterange'  style='width:54% !important'>
					<span>
					<input type="text" size="10" name="modifiedfrom4" id='modifiedfrom4' class="groupName" value=""
						maxlength="10" validate="calender" /> <img
						title="Shared From Date" alt="Shared From Date"
						src="../framework/skins/hhsa/images/calender.png"
						onclick="NewCssCal('modifiedfrom4',event,'mmddyyyy');return false;" />
					/<input type="text" size="10" name="modifiedto4" id='modifiedto4' class="groupName" value=""
						maxlength="10" validate="calender" /> <img
						title="Shared To Date" alt="Shared To Date"
						src="../framework/skins/hhsa/images/calender.png"
						onclick="NewCssCal('modifiedto4',event,'mmddyyyy');return false;" /></span><br>
						<span class="error" for="modifiedfrom4" style='width:62% !important;float: right;'></span>
						<span class="error" for="modifiedto4" style='width:62% !important;float: right;' ></span>
						</span>
			</div>
		</div>
	</div>
		<div class="buttonholder">
			<input type="button" id="clearfilter3" title="Reset all the search criteria fields" value="Clear Fields" onclick="clearFilter()" class="graybtutton" />
			<input type="submit" value="Search" title="Search" id="filter4"/>
		</div>	
				<div style="clear: both"></div>
		
 </div>
</div>
</form>
</c:if>	
<div style="clear: both"></div>
<!-- End -->

<div class='hiddenBlock'>
	<ol id= "docTypeagency_org">
		<c:forEach items="${docTypeAgency}" var="entry">
	        <li class="ddcombo_event data">${entry}</li>
	    </c:forEach>
	</ol>
	<ol id= "docTypeprovider_org">
		<c:forEach items="${docTypeProvider}" var="entry">
	        <li class="ddcombo_event data">${entry}</li>
	    </c:forEach>
	</ol>
	<ol id= "docTypecity_org">
		<c:forEach items="${docTypeCity}" var="entry">
	        <li class="ddcombo_event data">${entry}</li>
	    </c:forEach>
	</ol>
</div>