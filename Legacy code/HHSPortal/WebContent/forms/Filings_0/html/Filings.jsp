<%@ taglib prefix="fb" uri="/WEB-INF/tld/formbuilder-taglib.tld"%><%@ page errorPage="/error/errorpage.jsp" %> <%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%><html lang="en"><portlet:defineObjects /><head><title>Untitled</title><meta http-equiv="Content-Type" content="text/html; charset=utf-8"></meta>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/autoNumeric-1.7.5.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/forms/Filings_0/js/fb_Filings_1358244565655.js" charset="utf-8"></script>
	<script type="text/javascript">
		var actionUrl  = '<%=renderResponse.createActionURL()%>'+"&business_app_id="+'<%=renderRequest.getAttribute("business_app_id")%>'+"&section="+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>';
	function submitForm(actionParameter) {
		$("input[type='hidden'],input[type='text'], textarea").each(function(){
  		  convertSpecialCharactersHTMLGlobal($(this).attr("id"),true);
  	  	});
		document.Filings.action = actionUrl + '&next_action=' + actionParameter;
		document.Filings.submit();
	}
	
</script>
<style>
select{
width: 250px;
}
* {
	padding: 0px;
}
</style>
</head>
<body Style="padding:0mm 0mm; margin:0cm 0cm 0cm 0cm " bgcolor="#FFFFFF">
   <div id="fb_main" class="class_main formcontainer">
      <form action="<portlet:actionURL/>" method="post" name="Filings">
         <input type="hidden" name="corpStr" value="${corpStr}"/><input type="hidden" name="fb_formName" value="Filings"></input><input type="hidden" name="fb_fileName" value="Filings"></input><input type="hidden" name="fb_formVersion" value="0"></input><input type="hidden" name="fb_language" value="English"></input><input type="hidden" name="fb_languageCode" value="en"></input><span id="property" password=" " name="property" tablename="FILING_FORM" defaultpermission="view" convertdropdowntotextbox="false" makesqlfieldreadonly="false" fileunit="mb" filesize="2" backgroundcolor="FFFFFF" url=" " driverclass=" " username=" " class="class_0">
         </span>
         <div id="fb_divBl0ck1" class="class_1"></div>
         <div id="fb_divBl0ck2" class="class_2">
            <fb:line id="lineElt21341750260550dIi15v" name="div_row_q" visibilityvalue="Visible" condition="condition" lineid="undefined" sectionname=" " y2="75" y1="5" x2="1670" x1="0">
               <div>
                  <h2 style="float:none">Filings Questions</h2>
               </div>
               <div style="min-height:3px; background-color:black; width:80%"></div>
               <div id="txtElt11342012906940dIi15v" lineid="lineElt21341750260550dIi15v" sectionname=" " y2="63" y1="30" x2="873" x1="20" tag="div" pos_rel="false" selectedindex="0" class="class_10">
                  <span id="txtElt11342012906940dIi15v_textElement" class="class_12">
                     <p>Please fill out the form below as accurately as possible. This information will determine what documents you are required to upload.</p>
                     <p><font color="#ff0000">*</font> Indicates required fields</p>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck3" class="class_13"></div>
         <div id="fb_divBl0ck4" class="class_14">
            <fb:line id="lineElt11341750060396dIi15v" name="div_row_q0" visibilityvalue="Visible" condition="'{$corpStr}' == 'Non Profit'" lineid="undefined" sectionname=" " y2="203" y1="76" x2="1670" x1="0">
               <div id="txtElt11340776614963dIi15v" lineid="lineElt11341750060396dIi15v" sectionname=" " y2="119" y1="2" x2="862" x1="21" tag="div" pos_rel="false" selectedindex="0" class="class_15">
                  <span id="txtElt11340776614963dIi15v_textElement" class="class_17">
                     <p>
                        <span><strong>New York State Attorney General Charities Registration</strong></span>
                     </p>
                     <p> </p>
                     <p>
                        <span>All charitable organizations operating in New York State are required by law to register and file annual financial reports with the Attorney General's Office. This includes any organization that conducts charitable activities, holds property that is used for charitable purposes, or solicits financial or other contributions. Unless you are exempt, you must have an up-to-date New York State Attorney General Charities Bureau (Charities Bureau) registration before competing to do business with the City of New York.</span>
                     </p>
                     <p class="class_18"><a href="http://www.charitiesnys.com/home.jsp" target="_blank"><font color="#333399">Click here for more information or to register</font></a></p>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck5" class="class_19"></div>
         <div id="fb_divBl0ck6">
            <div id="errorEltDiv" y1="0" y2="0" x1="0" x2="0"></div>
         </div>
         <div id="fb_divBl0ck7" class="class_20"></div>
         <div id="fb_divBl0ck8" class="class_21">
            <fb:line id="lineElt11340776947635dIi15v" name="div_row_q1" visibilityvalue="Visible" condition="'{$corpStr}' == 'Non Profit'" lineid="undefined" sectionname=" " y2="258" y1="213" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt11340776947634dIi15v_label" title="Is your organization exempt from filing with the Charities Bureau?" tag="label" for="dropdownElt11340776947637dIi15v_select" class="class_23">Is your organization exempt from filing with the Charities Bureau?</label>
                  </span>
                  <span class="formfield">
                     <fb:select id="dropdownElt11340776947637dIi15v_select" name="orgExempt" cssclass="class_25" optionsize="3" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="Is your organization exempt from filing with the Charities Bureau?" tabindex="1" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="ORG_EXEMPT" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt11340776947635dIi15v">
                        <option value=" " title=" " selected="selected">Select</option>
                        <option value="Yes" title="Yes">Yes</option>
                        <option value="No" title="No">No</option>
                     </fb:select>
                  </span>
                  <span class="formfield error">
                     <fb:error id="ORG_EXEMPT" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck9" class="class_28"></div>
         <div id="fb_divBl0ck10" class="class_29">
            <fb:line id="lineElt11340778636846dIi15v" name="div_row_q2" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="392" y1="259" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required" tag="span">*</span>
                  <label id="labelElt1340778645083dIi15v_label" title="Reasons for exemption" tag="label" for="checkboxElt11340778636849dIi15v_checkbox">Reasons for exemption:</label>
                  </span>
                  <span class="formfield">
                     <div>
                        <fb:input id="checkboxElt11340778636849dIi15v_checkbox" type="checkbox" name="Religious_Organization" value="Religious_Organization" editrole=",semi_edit,admin,edit" title="Religious Organization" tabindex="2" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="RELIGIOUS_ORGANIZATION" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt11340778636846dIi15v"></fb:input>
                        <label>
                        Religious Organization
                        </label>
                     </div>
                     <div>
                        <fb:input id="checkboxElt21340778702944dIi15v_checkbox" type="checkbox" name="Educational_Institute" value="Educational_Institute" editrole=",semi_edit,admin,edit" title="Educational Institution" tabindex="3" viewrole=",read_only,semi_edit" errorcode=" Educational_Institute" schemaname="EDUCATION_INSTITUTE" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt11340778636846dIi15v"></fb:input>
                        <label>
                        Educational Institution
                        </label>
                     </div>
                     <div>
                        <fb:input id="checkboxElt21340779203866dIi15v_checkbox" type="checkbox" name="Historical_Societies" value="Historical_Societies" editrole=",semi_edit,admin,edit" title="Historical Societies chartered by the New York State Board of Regents" tabindex="5" viewrole=",read_only,semi_edit" errorcode=" Historical_Societies" schemaname="HISTORICAL_SOCIETIES" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt11340778636846dIi15v"></fb:input>
                        <label>
                        Historical Societies chartered by the New York State Board of Regents
                        </label>
                     </div>
                     <div>
                        <fb:input id="checkboxElt21340779008559dIi15v_checkbox" type="checkbox" name="Patriotic" value="Patriotic" editrole=",semi_edit,admin,edit" title="Fraternal, Patriotic, Social, Alumni, Law Enforcement Support Organizations" events="Patriotic" tabindex="4" viewrole=",read_only,semi_edit" errorcode=" Patriotic" schemaname="PATRIOTIC" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt11340778636846dIi15v"></fb:input>
                        <label>
                        Fraternal, Patriotic, Social, Alumni, Law Enforcement Support Organizations
                        </label>
                     </div>
                     <div>
                        <fb:input id="checkboxElt11340781903926dIi15v_checkbox" type="checkbox" name="other_exempt" value="other_exempt" editrole=",semi_edit,admin,edit" title="Other" tabindex="7" viewrole=",read_only,semi_edit" errorcode=" other_exempt" schemaname="OTHER_EXEMPTIONS" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt11340778636846dIi15v"></fb:input>
                        <label>
                        Other
                        </label>
                     </div>
                     <div>
                        <fb:input id="checkboxElt11340779531757dIi15v_checkbox" type="checkbox" name="Relief" value="Relief" editrole=",semi_edit,admin,edit" title="Relief of an individual" tabindex="6" viewrole=",read_only,semi_edit" errorcode=" Relief" schemaname="RELIEF" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt11340778636846dIi15v"></fb:input>
                        <label>
                        Relief of an individual
                        </label>
                     </div>
                  </span>
                  <span class="formfield error">
                     <fb:error id="RELIGIOUS_ORGANIZATION" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck11" class="class_68"></div>
         <div id="fb_divBl0ck12" class="class_69">
            <fb:line id="lineElt11340782345688dIi15v" name="div_row_q3" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="502" y1="393" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340782367054dIi15v_label" title="If other, please explain" tag="label" for="textareaElt11340782345692dIi15v_textarea">If other, please explain:</label>
                  </span>
                  <span class="formfield" maxlength="250">
                     <fb:textarea id="textareaElt11340782345692dIi15v_textarea" name="other_exempt_explain" cssclass=" input class_72" value="" editrole=",semi_edit,admin,edit" title="If other, please explain" tabindex="8" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="OTHER_EXEMP_EXPLAIN" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt11340782345688dIi15v" cols="32" rows="2"></fb:textarea>
                  </span>
                  <span class="formfield error">
                     <fb:error id="OTHER_EXEMP_EXPLAIN" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck13" class="class_75"></div>
         <div id="fb_divBl0ck14" class="class_76">
            <fb:line id="lineElt11340783082322dIi15v" name="div_row_q4" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="546" y1="503" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340783112186dIi15v_label" title="Please enter your organization's Registration ID#" tag="label" for="textboxElt11340783082325dIi15v_textbox" class="class_78">Please enter your organization's Registration ID #:</label>
                  </span>
                  <span class="formfield">
                     <fb:input id="textboxElt11340783082325dIi15v_textbox" type="text" maxlength="8" size="15" defaultReadOnly="false" name="org_reg_id" cssclass=" input class_80" value="" editrole=",semi_edit,admin,edit" title="A registration ID # is provided to organizations registered with the Charities Bureau. You may look up your organization's number by searching the Charities Bureau Registry" tabindex="9" viewrole=",read_only,semi_edit" errorcode=" ! This field is required. Organizations Registration ID field is Mandatory. Please fill to proceed." schemaname="ORG_REG_ID" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt11340783082322dIi15v"></fb:input>
                  </span>
                  <span class="formfield error">
                     <fb:error id="ORG_REG_ID" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck15" class="class_83"></div>
         <div id="fb_divBl0ck16" class="class_84">
            <fb:line id="lineElt11340783350444dIi15v" name="div_row_q5" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="579" y1="547" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340783549206dIi15v_label" title="Registration Type" tag="label" for="dropdownElt11340783350449dIi15v_select">Registration Type:</label>
                  </span>
                  <span class="formfield">
                     <fb:select id="dropdownElt11340783350449dIi15v_select" name="Registration_Type" cssclass="class_87" optionsize="3" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="Registration Type" tabindex="10" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="REGISTRATION_TYPE" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt11340783350444dIi15v">
                        <option value=" " title=" " selected="selected">Select</option>
                        <option value="Estate Power and Trust Law" title="Estate Power and Trust Law">Estate Power and Trust Law</option>
                        <option value="Dual/Executive Law Article 7-A (7A)" title="Dual/Executive Law Article 7-A (7A)">Dual/Executive Law Article 7-A (7A)</option>
                     </fb:select>
                  </span>
                  <span class="formfield error">
                     <fb:error id="REGISTRATION_TYPE" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck17" class="class_90"></div>
         <div id="fb_divBl0ck18" class="class_91">
            <fb:line id="lineElt11340783635269dIi15v" name="div_row_q6" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="678" y1="580" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340784064477dIi15v_label" title="Is your organization's registration with the Charities Bureau up-to-date? You will be required to upload your organization's complete CHAR500 filing, including IRS Form 990 and Audit/Review Report" tag="label" for="checkboxElt11340783635273dIi15v_checkbox">Is your organization's registration with the Charities Bureau up-to-date? You will be required to upload your organization's complete CHAR500 filing, including IRS Form 990 and Audit/Review Report:</label>
                  </span>
                  <span class="formfield">
                     <div>
                        <fb:input id="checkboxElt11340783635273dIi15v_checkbox" type="checkbox" name="org_reg_with_char" value="org_reg_with_char" editrole=",semi_edit,admin,edit" title="Your Charities Bureau Registration must be up-to-date in order to complete your HHS Accelerator Application" tabindex="11" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="ORG_REG_WITH_CHARI" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt11340783635269dIi15v"></fb:input>
                        <label>
                        Yes, my organization's registration with the Charities Bureau is up to date
                        </label>
                     </div>
                  </span>
                  <span class="formfield error">
                     <fb:error id="ORG_REG_WITH_CHARI" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck19" class="class_100"></div>
         <div id="fb_divBl0ck20" class="class_101">
            <fb:line id="lineElt11340784181689dIi15v" name="div_row_q7" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="768" y1="679" x2="1670" x1="0">
          	<!-- Start of changes for Release 3.10.0 . Enhancement 6573 - Remove CHAR500 extension logic -->
          	<!--  <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340785012290dIi15v_label" title="Is your organization currently under an extension period for your CHAR500?" tag="label" for="radiogroupElt11340784181694dIi15v_radio">Is your organization currently under an extension period for your CHAR500?</label>
                  </span>
                  <span class="formfield">
                     <div>
                        <fb:input id="radiogroupElt11340784181694dIi15v_radio" type="radio" name="char_ext" value="char_ext_first" editrole=",semi_edit,admin,edit" title="Yes" events="char_ext_first" tabindex="12" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="NO_SEC_CHAR_EXT" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt11340784181689dIi15v"></fb:input>
                        <label>
                        Yes
                        </label>
                     </div>
                     <div>
                        <fb:input id="radiogroupElt21340784265528dIi15v_radio" style="display:none" type="radio"  name="char_ext" value="char_ext_sec" editrole=",semi_edit,admin,edit" title="Yes, this is the secound extension" events="char_ext_first" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="NO_SEC_CHAR_EXT" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt11340784181689dIi15v"></fb:input>
                        <label style="display:none">
                        Yes, this is the second extension
                        </label>
                     </div>
                     <div>
                        <fb:input id="radiogroupElt21340784627848dIi15v_radio" type="radio" name="char_ext" value="no_extension" editrole=",semi_edit,admin,edit" title="No" events="char_ext_first" tabindex="14" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="NO_SEC_CHAR_EXT" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt11340784181689dIi15v"></fb:input>
                        <label>
                        No
                        </label>
                     </div>
                  </span>
                  <span class="formfield error">
                     <fb:error id="NO_SEC_CHAR_EXT" cssclass="error"></fb:error>
                  </span>
               </div> -->  
               <!-- End of changes for Release 3.10.0 . Enhancement 6573 - Remove CHAR500 extension logic -->
            </fb:line>
         </div>
         <div id="fb_divBl0ck21" class="class_122"></div>
         <div id="fb_divBl0ck22" class="class_123">
            <fb:line id="lineElt11340785885291dIi15v" name="div_row_q8" visibilityvalue="visibilityvalue" condition="condition" lineid="undefined" sectionname=" " y2="792" y1="769" x2="1670" x1="0">
               <div>
                  <h3>Tax Filing</h3>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck23" class="class_127"></div>
         <div id="fb_divBl0ck24" class="class_128">
            <fb:line id="lineElt21340785992099dIi15v" name="div_row_q9" visibilityvalue="Visible" condition="'{$corpStr}' == 'Non Profit'" lineid="undefined" sectionname=" " y2="857" y1="793" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340786085250dIi15v_label" title="Has your organization been determined tax exempt by the Internal Revenue Service (IRS)?" tag="label" for="dropdownElt11340785885294dIi15v_select" class="class_130">Has your organization been determined tax exempt by the Internal Revenue Service (IRS)?</label>
                  </span>
                  <span class="formfield">
                     <fb:select id="dropdownElt11340785885294dIi15v_select" name="irs" cssclass="class_133" optionsize="3" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="Has your organization been determined tax exempt by the Internal Revenue Service (IRS)?" tabindex="14" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="IRS" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt21340785992099dIi15v">
                        <option value=" " title=" " selected="selected">Select</option>
                        <option value="Yes" title="Yes">Yes</option>
                        <option value="No" title="No">No</option>
                     </fb:select>
                  </span>
                  <span class="formfield error">
                     <fb:error id="IRS" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck25" class="class_135"></div>
         <div id="fb_divBl0ck26" class="class_136">
            <fb:line id="lineElt31340786067850dIi15v" name="div_row_q10" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="934" y1="858" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340787030382dIi15v_label" title="If no, please indicate which action has occurred:" tag="label" for="radiogroupElt11340786346970dIi15v_radio">If no, please indicate which action has occurred:</label>
                  </span>
                  <span class="formfield">
                     <div>
                        <fb:input id="radiogroupElt11340786346970dIi15v_radio" type="radio" name="revoked_actions" value="revoked" editrole=",semi_edit,admin,edit" title="Revoked" events="revoked" tabindex="16" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="REVOKED" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt31340786067850dIi15v"></fb:input>
                        <label>
                        Revoked
                        </label>
                     </div>
                     <div>
                        <fb:input id="radiogroupElt21340786404448dIi15v_radio" type="radio" name="revoked_actions" value="pending" editrole=",semi_edit,admin,edit" title="Application Pending " events="revoked" tabindex="17" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="REVOKED" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt31340786067850dIi15v"></fb:input>
                        <label>
                        Application Pending
                        </label>
                     </div>
                     <div>
                        <fb:input id="radiogroupElt21340786592618dIi15v_radio" type="radio" name="revoked_actions" value="not_applied" editrole=",semi_edit,admin,edit" title="Not Applied" events="revoked" tabindex="18" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="REVOKED" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt31340786067850dIi15v"></fb:input>
                        <label>
                        Not Applied
                        </label>
                     </div>
                  </span>
                  <span class="formfield error">
                     <fb:error id="REVOKED" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck27" class="class_157"></div>
         <div id="fb_divBl0ck28" class="class_158">
            <fb:line id="lineElt11340787157164dIi15v" name="div_row_q11" visibilityvalue="Visible" condition="condition" lineid="undefined" sectionname=" " y2="979" y1="935" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340788051941dIi15v_label" title="Is your organization current in its tax filing(s)?" tag="label" for="dropdownElt11340787157168dIi15v_select" class="class_160">Is your organization current in its tax filing(s)?</label>
                  </span>
                  <span class="formfield">
                     <fb:select id="dropdownElt11340787157168dIi15v_select" name="curr_tax_filings" cssclass="class_162" optionsize="4" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="Is your organization current in its tax filing(s)?" tabindex="19" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="CURR_TAX_FILINGS" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt11340787157164dIi15v">
                        <option value=" " title=" " selected="selected">Select</option>
                        <option value="Yes" title="Yes">Yes</option>
                        <option value="No" title="No">No</option>
                        <option value="Exempt From Filing" title="Exempt From Filing">Exempt From Filing</option>
                     </fb:select>
                  </span>
                  <span class="formfield error">
                     <fb:error id="CURR_TAX_FILINGS" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck29" class="class_165"></div>
         <div id="fb_divBl0ck30" class="class_166">
            <fb:line id="lineElt21340787789494dIi15v" name="div_row_q12" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="1089" y1="980" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                    <span class="required">*</span>
                  <label id="labelElt1340788442544dIi15v_label" title="If no or exempt, please explain" tag="label" for="textareaElt1340788255147dIi15v_textarea">If no or exempt, please explain:</label>  
                  </span>
                  <span class="formfield" maxlength="150">
                  	<fb:textarea id="textareaElt1340788255147dIi15v_textarea" name="curr_tax_explain" cssclass=" input class_168" value="" editrole=",semi_edit,admin,edit" title="If no or exempt, please explain" tabindex="20" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="CURR_TAX_EXPLAIN" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340787789494dIi15v" cols="32" rows="2"></fb:textarea>
                  </span>
                  <span class="formfield error">
                     <fb:error id="CURR_TAX_EXPLAIN" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck31" class="class_172"></div>
         <div id="fb_divBl0ck32" class="class_173">
            <fb:line id="lineElt11340788218373dIi15v" name="div_row_q13" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="1154" y1="1090" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340789598078dIi15v_label" title="Select your organization's most recent filing type for the IRS Form 990" for="radiogroupElt11340788852126dIi15v_radio">Select your organization's most recent filing type for the IRS Form 990:</label>
                  </span>  
                  <span class="formfield" maxlength="150">
                     <div>
                        <fb:input id="radiogroupElt11340788852126dIi15v_radio" type="radio" name="irs_form" value="irs_form_990" editrole=",semi_edit,admin,edit" title="IRS Form 990" events="irs_form" tabindex="20" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="IRS_FORM_990EXT" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt11340788218373dIi15v"></fb:input>
                        <label>
                        IRS Form 990
                        </label>
                     </div>
                     <div>
                        <fb:input id="radiogroupElt21340788900360dIi15v_radio" type="radio" name="irs_form" value="irs_form_990n" editrole=",semi_edit,admin,edit" title="IRS Form 990 N" events="irs_form" tabindex="21" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="IRS_FORM_990EXT" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt11340788218373dIi15v"></fb:input>
                        <label>
                        IRS Form 990 N
                        </label>
                     </div>
                     <div>
                        <fb:input id="radiogroupElt1340789454638dIi15v_radio" type="radio" name="irs_form" value="irs_form_990ext" editrole=",semi_edit,admin,edit" title="IRS Form 990-Extension Document" events="irs_form" tabindex="20" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="IRS_FORM_990EXT" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt11340788218373dIi15v"></fb:input>
                        <label>
                        IRS Form 990-Extension Document
                        </label>
                     </div>
                     <div>
                        <fb:input id="radiogroupElt1340789283352dIi15v_radio" type="radio" name="irs_form" value="irs_form_990ez" editrole=",semi_edit,admin,edit" title="IRS Form 990 EZ" events="irs_form" tabindex="20" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="IRS_FORM_990EXT" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt11340788218373dIi15v"></fb:input>
                        <label>
                        IRS Form 990 EZ
                        </label>
                     </div>
                  </span>
                  <span class="formfield error">
                     <fb:error id="IRS_FORM_990EXT" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck33" class="class_200"></div>
         <div id="fb_divBl0ck34" class="class_201">
            <fb:line id="lineElt11340788852122dIi15v" name="div_row_q14" visibilityvalue="visibilityvalue" condition="condition" lineid="undefined" sectionname=" " y2="1179" y1="1155" x2="1670" x1="0">
               <div>
                  <h3>Financial Statements and Audit</h3>
               </div>
            </fb:line>
         </div>
         <!-- Start of changes for Release 3.10.0 . Enhancement 6572 -->
         <div id="fb_divBl0ck56" class="class_291"></div>
         <div id="fb_divBl0ck57" class="class_292">
            <fb:line id="lineElt_new" name="div_row_q25" visibilityvalue="Visible" condition="condition" lineid="undefined" sectionname=" " y2="1258" y1="1180" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt_label_new" title="What is your organization's annual operating budget?" tag="label" for="textBoxElt_textbox_new" class="class_208">What is your organization's annual operating budget?</label>
                  </span>
                  <span class="formfield">
                     <fb:input id="textBoxElt_textbox_new" name="annual_operating_budget" type="text" maxlength="500" size="34" defaultReadOnly="false" cssclass="input class_293" value="" editrole=",semi_edit,admin,edit" title="What is your organization's annual operating budget?" tabindex="23" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="ANNUAL_OPERATING_BUDGET" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt_new"></fb:input>
                  </span>
                  <span class="formfield error">
                     <fb:error id="ANNUAL_OPERATING_BUDGET" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
          <!-- End of changes for Release 3.10.0 . Enhancement 6572 -->
         <div id="fb_divBl0ck35" class="class_205"></div>
         <div id="fb_divBl0ck36" class="class_206">
            <fb:line id="lineElt11340789748052dIi15v" name="div_row_q15" visibilityvalue="Visible" condition="condition" lineid="undefined" sectionname=" " y2="1258" y1="1180" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340790165284dIi15v_label" title="Has an independent certified public accountant either reviewed your organization's most recent annual financial statement or completed an audit?" tag="label" for="radiogroupElt11340791887124dIi15v_radio" class="class_208">Has an independent certified public accountant either reviewed your organization's most recent annual financial statement or completed an audit?</label>
                  </span>
                  <span class="formfield">
                     <fb:select id="dropdownElt11340789748055dIi15v_select" name="select_completed_audit" cssclass="class_210" optionsize="3" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="Has an independent certified public accountant either reviewed your organization's most recent annual financial statement or completed an audit?" tabindex="23" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="SELECT_COMPLETED_AUDIT" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt11340789748052dIi15v">
                        <option value=" " title=" " selected="selected">Select</option>
                        <option value="Yes" title="Yes">Yes</option>
                        <option value="No" title="No">No</option>
                     </fb:select>
                  </span>
                  <span class="formfield error">
                     <fb:error id="SELECT_COMPLETED_AUDIT" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck37" class="class_213"></div>
         <div id="fb_divBl0ck38" class="class_214">
            <fb:line id="lineElt11340791578841dIi15v" name="div_row_q16" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="1310" y1="1259" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340792286093dIi15v_label" title="If yes, please indicate which action has occurred" tag="label" for="radiogroupElt11340791887124dIi15v_radio" class="class_216">If yes, please indicate which action has occurred:</label>
                  </span>
                  <span class="formfield">
                     <div>
                        <fb:input id="radiogroupElt11340791887124dIi15v_radio" type="radio" name="completed_audit" value="CPA_Review_Report" editrole=",semi_edit,admin,edit" title="CPA Review Report" events="CPA_Review_Report" tabindex="23" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="INDEPENDENT_AUDIT" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt11340791578841dIi15v"></fb:input>
                        <label>
                        CPA Review Report
                        </label>
                     </div>
                     <div>
                        <fb:input id="radiogroupElt21340791896885dIi15v_radio" type="radio" name="completed_audit" value="Independent_Audit" editrole=",semi_edit,admin,edit" title="Independent Audit" events="CPA_Review_Report" tabindex="24" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="INDEPENDENT_AUDIT" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt11340791578841dIi15v"></fb:input>
                        <label>
                        Independent Audit
                        </label>
                     </div>
                  </span>
                  <span class="formfield error">
                     <fb:error id="INDEPENDENT_AUDIT" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck39" class="class_230"></div>
         <div id="fb_divBl0ck40" class="class_231">
            <fb:line id="lineElt11340791887119dIi15v" name="div_row_q17" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="1344" y1="1311" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340792913356dIi15v_label" title="Were there Material Findings?" tag="label" for="dropdownElt1340792805081dIi15v_select">Were there Material Findings?</label>
                  </span>
                  <span class="formfield">
                     <fb:select id="dropdownElt1340792805081dIi15v_select" name="where_material_findings" cssclass="class_235" optionsize="3" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="Were there Material Findings?" tabindex="26" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="WERE_MATERIAL_FINDINGS" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt11340791887119dIi15v">
                        <option value=" " title=" " selected="selected">Select</option>
                        <option value="Yes" title="Yes">Yes</option>
                        <option value="No" title="No">No</option>
                     </fb:select>
                  </span>
                  <span class="formfield error">
                     <fb:error id="WERE_MATERIAL_FINDINGS" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck41" class="class_237"></div>
         <div id="fb_divBl0ck42" class="class_238">
            <fb:line id="lineElt21340792728097dIi15v" name="div_row_q18" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="1453" y1="1345" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340792622118dIi15v_label" title="If yes, please explain Material Findings" tag="label" for="textareaElt1340792526857dIi15v_textarea">If yes, please explain Material Findings:</label>
                  </span>
                  <span class="formfield" maxlength="150">
                     <fb:textarea id="textareaElt1340792526857dIi15v_textarea" name="material_findings" cssclass=" input class_241" value="" editrole=",semi_edit,admin,edit" title="If yes, please explain Material Findings" tabindex="25" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="MATERIAL_FINDINGS" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340792728097dIi15v" cols="32" rows="2"></fb:textarea>
                  </span>
                  <span class="formfield error">
                     <fb:error id="MATERIAL_FINDINGS" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck43" class="class_244"></div>
         <div id="fb_divBl0ck44" class="class_245">
            <fb:line id="lineElt21340793003321dIi15v" name="div_row_q19" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="1486" y1="1454" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required" tag="span">*</span>
                  <label id="labelElt1340793254791dIi15v_label" title="If no, please provide a reason:" tag="label" for="dropdownElt1340793157977dIi15v_select">If no, please provide a reason:</label>
                  </span>
                  <span class="formfield">
                     <fb:select id="dropdownElt1340793157977dIi15v_select" name="no_material_findings" cssclass="class_248" optionsize="3" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="If no, please provide a reason" tabindex="26" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="NO_MATERIAL_FINDINGS" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt21340793003321dIi15v">
                        <option value=" " title=" " selected="selected">Select</option>
                        <option value="Revenue does not exceed threshold requiring audit" title="Revenue does not exceed threshold requiring audit">Revenue does not exceed threshold requiring audit</option>
                        <option value="Other" title="Other">Other</option>
                     </fb:select>
                  </span>
                  <span class="formfield error">
                     <fb:error id="NO_MATERIAL_FINDINGS" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck45" class="class_251"></div>
         <div id="fb_divBl0ck46" class="class_252">
            <fb:line id="lineElt11340793139145dIi15v" name="div_row_q20" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="1595" y1="1487" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340793482190dIi15v_label" title="If other, please provide a reason" tag="label" for="textareaElt1340793415886dIi15v_textarea">If other, please provide a reason:</label>
                  </span>
                  <span class="formfield" maxlength="150">
                     <fb:textarea id="textareaElt1340793415886dIi15v_textarea" name="other_material_findings" cssclass=" input class_255" value="" editrole=",semi_edit,admin,edit" title="If other, please provide a reason" tabindex="25" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="OTHER_MATERIAL_FINDINGS" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt11340793139145dIi15v" cols="32" rows="2"></fb:textarea>
                  </span>
                  <span class="formfield error">
                     <fb:error id="OTHER_MATERIAL_FINDINGS" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck47" class="class_258"></div>
         <div id="fb_divBl0ck48" class="class_259">
            <fb:line id="lineElt11340793588494dIi15v" name="div_row_q21" visibilityvalue="Visible" condition="condition" lineid="undefined" sectionname=" " y2="1641" y1="1596" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340794531466dIi15v_label" title="An A133 Single Audit is required for organizations that receive $750,000 or more in awards or grants from the Federal Government. " for="dropdownElt1340794397338dIi15v_select">Does your organization file an A133, Single Audit?</label>
                  </span>
                  <span class="formfield">
                     <fb:select id="dropdownElt1340794397338dIi15v_select" name="a133_single_audit" cssclass="class_263" optionsize="3" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="An A133 Single Audit is required for organizations that receive $750,000 or more in awards or grants from the Federal Government. " tabindex="28" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="A133_SINGLE_AUDIT" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt11340793588494dIi15v">
                        <option value=" " title=" " selected="selected">Select</option>
                        <option value="Yes" title="Yes">Yes</option>
                        <option value="No" title="No">No</option>
                     </fb:select>
                  </span>
                  <span class="formfield error">
                     <fb:error id="A133_SINGLE_AUDIT" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck49" class="class_265"></div>
         <div id="fb_divBl0ck50" class="class_266">
            <fb:line id="lineElt11340794618758dIi15v" name="div_row_q22" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="1680" y1="1642" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340794764144dIi15v_label" title="Were there Material Findings" tag="label" for="dropdownElt1340794655699dIi15v_select">Were there Material Findings</label>
                  </span>
                  <span class="formfield">
                     <fb:select id="dropdownElt1340794655699dIi15v_select" name="where_a133_single_audit" cssclass="class_269" optionsize="3" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="Were there Material Findings" tabindex="29" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="A133_WERE_MATERIAL_FINDINGS" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt11340794618758dIi15v">
                        <option value=" " title=" " selected="selected">Select</option>
                        <option value="Yes" title="Yes">Yes</option>
                        <option value="No" title="No">No</option>
                     </fb:select>
                  </span>
                  <span class="formfield error">
                     <fb:error id="A133_WERE_MATERIAL_FINDINGS" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck51" class="class_272"></div>
         <div id="fb_divBl0ck52" class="class_273">
            <fb:line id="lineElt21340794625483dIi15v" name="div_row_q23" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="1790" y1="1682" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340795057252dIi15v_label" title="If yes, please explain Material Findings" tag="label" for="textareaElt1340794973527dIi15v_textarea">If yes, please explain Material Findings:</label>
                  </span>
                  <span class="formfield" maxlength="150">
                     <fb:textarea id="textareaElt1340794973527dIi15v_textarea" name="yes_a133_material_findings" cssclass=" input class_276" value="" editrole=",semi_edit,admin,edit" title="If yes, please explain Material Findings" tabindex="25" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="A133_NO_MATERIAL_FINDINGS" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340794625483dIi15v" cols="32" rows="2"></fb:textarea>
                  </span>
                  <span class="formfield error">
                     <fb:error id="A133_NO_MATERIAL_FINDINGS" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck53" class="class_279"></div>
         <div id="fb_divBl0ck54" class="class_280">
            <fb:line id="lineElt11340795124657dIi15v" name="div_row_q24" visibilityvalue="visibilityvalue" condition="condition" lineid="undefined" sectionname=" " y2="1836" y1="1791" x2="1670" x1="0">
               <span class="buttonholder">
                  <fb:button id="buttonElt11340795124659dIi15v_button" type="button" name="back" cssclass=" button graybtutton class_282" value="&lt;&lt; Back" editrole=",semi_edit,admin,edit" title="back" events="eventname" tabindex="27" viewrole=",read_only,semi_edit" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" lineid="lineElt11340795124657dIi15v" defaultpermission="view"></fb:button>
                  <fb:button id="buttonElt21340795161694dIi15v_button" type="button" name="save" cssclass=" button class_285" value="Save" editrole=",semi_edit,admin,edit" title="Save" events="eventname" tabindex="28" viewrole=",read_only,semi_edit" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" lineid="lineElt11340795124657dIi15v" defaultpermission="view"></fb:button>
                  <fb:button id="buttonElt21340795216285dIi15v_button" type="button" name="save_next" cssclass=" button class_288" value=" Save &amp; Next " editrole=",semi_edit,admin,edit" title="Save &amp; Next" events="eventname" tabindex="29" viewrole=",read_only,semi_edit" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" lineid="lineElt11340795124657dIi15v" defaultpermission="view"></fb:button>
               </span>
            </fb:line>
         </div>
         <div id="fb_divBl0ck55" class="class_290"></div>
         <fb:input id="fbjscounter" type="hidden" name="fbjscounter" value="1"></fb:input>
      </form>
   </div>
</body>
</html>