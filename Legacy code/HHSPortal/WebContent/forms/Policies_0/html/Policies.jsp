<%@ taglib prefix="fb" uri="/WEB-INF/tld/formbuilder-taglib.tld"%><%@ page errorPage="/error/errorpage.jsp" %> <%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%><html lang="en"><portlet:defineObjects /><head><title>Untitled</title><meta http-equiv="Content-Type" content="text/html; charset=utf-8"></meta>

<script type="text/javascript" src="${pageContext.servletContext.contextPath}/forms/Policies_0/js/fb_Policies_1358244577823.js" charset="utf-8"></script>
      <script type="text/javascript">
            var actionUrl  = '<%=renderResponse.createActionURL()%>'+"&business_app_id="+'<%=renderRequest.getAttribute("business_app_id")%>'+"&section="+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>';
      function submitForm(actionParameter) {
            $("input[type='hidden'],input[type='text'], textarea").each(function(){
              convertSpecialCharactersHTMLGlobal($(this).attr("id"),true);
            });
            document.Policies.action = actionUrl + '&next_action=' + actionParameter;
            document.Policies.submit();
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
      <form action="<portlet:actionURL/>" method="post" name="Policies">
         <input type="hidden" name="fb_formName" value="Policies"></input><input type="hidden" name="fb_fileName" value="Policies"></input><input type="hidden" name="fb_formVersion" value="0"></input><input type="hidden" name="fb_language" value="English"></input><input type="hidden" name="fb_languageCode" value="en"></input><span id="property" password=" " name="property" tablename="POLICIES_FORM" defaultpermission="view" convertdropdowntotextbox="false" makesqlfieldreadonly="false" fileunit="mb" filesize="2" backgroundcolor="FFFFFF" url=" " driverclass=" " username=" " class="class_0">
         </span>
         <div id="fb_divBl0ck1" class="class_1"></div>
         <div id="fb_divBl0ck2" class="class_2">
            <fb:line id="lineElt11341744193951dIi15v" name="div_row_q" visibilityvalue="Visible" condition="condition" lineid="undefined" sectionname=" " y2="87" y1="5" x2="1670" x1="0">
               <div>
                  <h2 style="float:none">Policies Questions</h2>
               </div>
               <div style="min-height:3px; background-color:black; width:80%"></div>
               <span id="txtElt21340864289506dIi15v_textElement" class="class_12">
                  <p><font face="verdana,geneva">Please fill out the form below as accurately as possible</font></p>
                  <p><font face="verdana,geneva"><font color="#ff0000">*</font> Indicates required fields</font></p>
               </span>
            </fb:line>
         </div>
         <div id="fb_divBl0ck3" class="class_13"></div>
         <div id="fb_divBl0ck4">
            <div id="errorEltDiv" y1="0" y2="0" x1="0" x2="0"></div>
         </div>
         <div id="fb_divBl0ck5" class="class_14"></div>
         <div id="fb_divBl0ck6" class="class_15">
            <fb:line id="lineElt11340864279728dIi15v" name="div_row_q0" visibilityvalue="Visible" condition="condition" lineid="undefined" sectionname=" " y2="126" y1="94" x2="1670" x1="0">
               <div>
                  <h3>Financial Controls</h3>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck7" class="class_19"></div>
         <div id="fb_divBl0ck8" class="class_20">
            <fb:line id="lineElt11340864782379dIi15v" name="div_row_q1" visibilityvalue="Visible" condition="condition" lineid="undefined" sectionname=" " y2="177" y1="127" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt11340864782378dIi15v_label" title="Does your organization require two individuals to sign each check?" tag="label" for="dropdownElt11340864782383dIi15v_select" class="class_22">Does your organization require two individuals to sign each check?</label>
                  </span>
                  <span class="formfield">
                     <fb:select id="dropdownElt11340864782383dIi15v_select" name="sign_check" cssclass="class_25" optionsize="3" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="Does your organization require two individuals to sign each check?" events="sign_check" tabindex="1" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="SIGN_CHECK" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt11340864782379dIi15v">
                        <option value=" " title=" " selected="selected">Select</option>
                        <option value="Yes" title="Yes">Yes</option>
                        <option value="No" title="No">No</option>
                     </fb:select>
                  </span>
                  <span class="formfield error">
                     <fb:error id="SIGN_CHECK" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck9" class="class_27"></div>
         <div id="fb_divBl0ck10" class="class_28">
            <fb:line id="lineElt11340865269240dIi15v" name="div_row_q2" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="223" y1="178" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340865452404dIi15v_label" title="If yes, indicate when two individuals are required to sign each check:" tag="label" for="dropdownElt1340865329905dIi15v_select" class="class_30">If yes, indicate when two individuals are required to sign each check:</label>
                  </span>       
                  <span class="formfield">
                     <fb:select id="dropdownElt1340865329905dIi15v_select" name="indicate_sign_check" cssclass="class_33" optionsize="3" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="If yes, indicate when two individuals are required to sign each check" events="indicate_sign_check" tabindex="2" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="INDICATE_SIGN_CHECK" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt11340865269240dIi15v">
                        <option value=" " title=" " selected="selected">Select</option>
                        <option value="All Checks" title="All Checks">All Checks</option>
                        <option value="Above a specified amount" title="Above a specified amount">Above a specified amount</option>
                     </fb:select>
                  </span>
                  <span class="formfield error">
                     <fb:error id="INDICATE_SIGN_CHECK" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck11" class="class_35"></div>
         <div id="fb_divBl0ck12" class="class_36">
            <fb:line id="lineElt21340865278602dIi15v" name="div_row_q3" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="258" y1="224" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required" tag="span">*</span>
                  <label id="labelElt1340865841519dIi15v_label" title="Enter amount" tag="label" for="textboxElt11340865699865dIi15v_textbox" class="class_38">Enter amount:</label>
                  </span>
                  <span class="formfield">
                     <fb:input id="textboxElt11340865699865dIi15v_textbox" type="text" maxlength="6" size="15" defaultReadOnly="false" name="enter_amount" cssclass=" input class_40" value="" editrole=",semi_edit,admin,edit" title="Enter amount" tabindex="3" viewrole=",read_only,semi_edit" errorcode=" ! This field is required. enter_amount" schemaname="ENTER_AMOUNT" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340865278602dIi15v"></fb:input>
                  </span>
                  <span class="formfield error">
                     <fb:error id="ENTER_AMOUNT" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck13" class="class_43"></div>
         <div id="fb_divBl0ck14" class="class_44">
            <fb:line id="lineElt11340865699861dIi15v" name="div_row_q4" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="372" y1="259" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required" tag="span">*</span>
                  <label id="labelElt1340866065051dIi15v_label" title="Please explain" tag="label" for="textareaElt11340865916606dIi15v_textarea" class="class_46">Please explain:</label>
                  </span>
                  <span class="formfield" maxlength="150">
                     <fb:textarea id="textareaElt11340865916606dIi15v_textarea" name="explain_amount" cssclass=" input class_48" value="" editrole=",semi_edit,admin,edit" title="Please explain" tabindex="4" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="PLEASE_EXPLAIN_AMOUNT" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt11340865699861dIi15v" cols="33" rows="2"></fb:textarea>
                  </span>
                  <span class="formfield error">
                     <fb:error id="PLEASE_EXPLAIN_AMOUNT" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck15" class="class_51"></div>
         <div id="fb_divBl0ck16" class="class_52">
            <fb:line id="lineElt11340866209322dIi15v" name="div_row_q5" visibilityvalue="visibilityvalue" condition="condition" lineid="undefined" sectionname=" " y2="435" y1="373" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340866337465dIi15v_label" title="Are different staff members responsible for authorizing and recording financial transactions?" tag="label" for="dropdownElt1340866260079dIi15v_select" class="class_54">Are different staff members responsible for authorizing and recording financial transactions?</label>
                  </span>
                  <span class="formfield">
                     <fb:select id="dropdownElt1340866260079dIi15v_select" name="record_financial" cssclass="class_57" optionsize="3" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="Are different staff members responsible for authorizing and recording financial transactions?" events="record_financial" tabindex="5" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="RECORD_FINANCIALS" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt11340866209322dIi15v">
                        <option value=" " title=" " selected="selected">Select</option>
                        <option value="Yes" title="Yes">Yes</option>
                        <option value="No" title="No">No</option>
                     </fb:select>
                  </span>
                  <span class="formfield error">
                     <fb:error id="RECORD_FINANCIALS" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck17" class="class_59"></div>
         <div id="fb_divBl0ck18" class="class_60">
            <fb:line id="lineElt21340866240629dIi15v" name="div_row_q6" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="549" y1="436" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340866786878dIi15v_label" title="Please explain" for="textareaElt1340866698647dIi15v_textarea" class="class_62">Please explain:</label>
                  </span>
                  <span class="formfield" maxlength="150">
                     <fb:textarea id="textareaElt1340866698647dIi15v_textarea" name="financial_explain" cssclass=" input class_64" value="" editrole=",semi_edit,admin,edit" title="Please explain" tabindex="6" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="FIN_TRANSACTION_EXPLAIN" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340866240629dIi15v" cols="33" rows="2"></fb:textarea>
                  </span>
                  <span class="formfield error">
                     <fb:error id="FIN_TRANSACTION_EXPLAIN" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck19" class="class_67"></div>
         <div id="fb_divBl0ck20" class="class_68">
            <fb:line id="lineElt11340866570872dIi15v" name="div_row_q7" visibilityvalue="visibilityvalue" condition="condition" lineid="undefined" sectionname=" " y2="586" y1="550" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340867602060dIi15v_label" title="How often are bank accounts reconciled?" tag="label" for="dropdownElt1340867484461dIi15v_select" class="class_70">How often are bank accounts reconciled?</label>
                  </span>
                  <span class="formfield">
                     <fb:select id="dropdownElt1340867484461dIi15v_select" name="acc_reconciled" cssclass="class_73" optionsize="6" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="How often are bank accounts reconciled?" events="acc_reconciled" tabindex="7" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="ACC_RECONCILED" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt11340866570872dIi15v">
                        <option value=" " title=" " selected="selected">Select</option>
                        <option value="Weekly" title="Weekly">Weekly</option>
                        <option value="Monthly" title="Monthly">Monthly</option>
                        <option value="Quarterly" title="Quarterly">Quarterly</option>
                        <option value="Annually" title="Annually">Annually</option>
                        <option value="Never" title="Never">Never</option>
                     </fb:select>
                  </span>
                  <span class="formfield error">
                     <fb:error id="ACC_RECONCILED" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck21" class="class_75"></div>
         <div id="fb_divBl0ck22" class="class_76">
            <fb:line id="lineElt11340867440807dIi15v" name="div_row_q8" visibilityvalue="visibilityvalue" condition="condition" lineid="undefined" sectionname=" " y2="631" y1="588" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340867818030dIi15v_label" title="Does your organization utilize an electronic system for accounting?" tag="label" for="dropdownElt1340867745408dIi15v_select" class="class_78">Does your organization utilize an electronic system for accounting?</label>
                  </span>
                  <span class="formfield">
                     <fb:select id="dropdownElt1340867745408dIi15v_select" name="elec_acc" cssclass="class_81" optionsize="3" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="Does your organization utilize an electronic system for accounting?" events="elec_acc" tabindex="8" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="ELEC_ACC" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt11340867440807dIi15v">
                        <option value=" " title=" " selected="selected">Select</option>
                        <option value="Yes" title="Yes">Yes</option>
                        <option value="No" title="No">No</option>
                     </fb:select>
                  </span>
                  <span class="formfield error">
                     <fb:error id="ELEC_ACC" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck23" class="class_83"></div>
         <div id="fb_divBl0ck24" class="class_84">
            <fb:line id="lineElt21340867693480dIi15v" name="div_row_q9" visibilityvalue="visibilityvalue" condition="condition" lineid="undefined" sectionname=" " y2="674" y1="632" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340868019150dIi15v_label" title="Does your organization use an outsourced payroll system?" tag="label" for="dropdownElt1340867915736dIi15v_select" class="class_86">Does your organization use an outsourced payroll system?</label>
                  </span>
                  <span class="formfield">
                     <fb:select id="dropdownElt1340867915736dIi15v_select" name="payroll_system" cssclass="class_89" optionsize="3" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="Does the organization use an outsourced payroll system?" events="payroll_system" tabindex="9" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="PAYROLL_SYSTEM" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt21340867693480dIi15v">
                        <option value=" " title=" " selected="selected">Select</option>
                        <option value="Yes" title="Yes">Yes</option>
                        <option value="No" title="No">No</option>
                     </fb:select>
                  </span>
                  <span class="formfield error">
                     <fb:error id="PAYROLL_SYSTEM" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck25" class="class_91"></div>
         <div id="fb_divBl0ck26" class="class_92">
            <fb:line id="lineElt11340869933851dIi15v" name="div_row_q10" visibilityvalue="visibilityvalue" condition="condition" lineid="undefined" sectionname=" " y2="711" y1="675" x2="1670" x1="0">
               <div>
                  <h3>Human Resources and Performance Management</h3>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck27" class="class_96"></div>
         <div id="fb_divBl0ck28" class="class_97">
            <fb:line id="lineElt21340869942226dIi15v" name="div_row_q11" visibilityvalue="visibilityvalue" condition="condition" lineid="undefined" sectionname=" " y2="1119" y1="712" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340878149552dIi15v_label" title="Which of the following policies and procedures does your organization have in place?(Please check all that apply)" tag="label" for="textareaElt1340866698647dIi15v_textarea" class="class_99">Which of the following policies and procedures does your organization have in place?(Please check all that apply)</label>
                  </span>
                  <span class="formfield">
                     <div>
                        <fb:input id="checkboxElt11340869933855dIi15v_checkbox" type="checkbox" name="ceo_policy" value="ceo_policy" editrole=",semi_edit,admin,edit" title="CEO Compensation Policy" tabindex="10" viewrole=",read_only,semi_edit" errorcode=" CEO Compensation Policy" schemaname="CEO_POLICY" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340869942226dIi15v"></fb:input>
                        <label>
                        CEO Compensation Policy
                        </label>
                     </div>
                     <div>
                        <fb:input id="checkboxElt1340870460961dIi15v_checkbox" type="checkbox" name="emp_policy" value="emp_policy" editrole=",semi_edit,admin,edit" title="Employee Loan Policy" tabindex="11" viewrole=",read_only,semi_edit" errorcode=" Employee Loan Policy" schemaname="EMP_POLICY" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340869942226dIi15v"></fb:input>
                        <label>
                        Employee Loan Policy
                        </label>
                     </div>
                     <div>
                        <fb:input id="checkboxElt1340875265787dIi15v_checkbox" type="checkbox" name="anti_nepo" value="anti_nepo" editrole=",semi_edit,admin,edit" title="Anti-Nepotism Policy" tabindex="12" viewrole=",read_only,semi_edit" errorcode=" Anti-Nepotism Policy" schemaname="ANTI_NEPO" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340869942226dIi15v"></fb:input>
                        <label>
                        Anti-Nepotism Policy
                        </label>
                     </div>
                     <div>
                        <fb:input id="checkboxElt1340875742088dIi15v_checkbox" type="checkbox" name="staff_code_conduct" value="staff_code_conduct" editrole=",semi_edit,admin,edit" title="Staff Code of Conduct" tabindex="13" viewrole=",read_only,semi_edit" errorcode=" Staff Code of Conduct" schemaname="STAFF_CODE_CONDUCT" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340869942226dIi15v"></fb:input>
                        <label>
                        Staff Code of Conduct
                        </label>
                     </div>
                     <div>
                        <fb:input id="checkboxElt1340876110214dIi15v_checkbox" type="checkbox" name="director_conflict" value="director_conflict" editrole=",semi_edit,admin,edit" title="Board of Directors Conflict of Interest Policy " tabindex="15" viewrole=",read_only,semi_edit" errorcode=" Board of Directors Conflict of Interest Policy" schemaname="DIRECTOR_CONFLICT" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340869942226dIi15v"></fb:input>
                        <label>
                        Board of Directors Conflict of Interest Policy
                        </label>
                     </div>
                     <div>
                        <fb:input id="checkboxElt1340876293783dIi15v_checkbox" type="checkbox" name="conflict_interest" value="conflict_interest" editrole=",semi_edit,admin,edit" title="Conflict of Interest Policy " tabindex="16" viewrole=",read_only,semi_edit" errorcode=" Continuity of Operations plan" schemaname="CONFLICT_INTEREST" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340869942226dIi15v"></fb:input>
                        <label>
                        Conflict of Interest Policy
                        </label>
                     </div>
                     <div>
                        <fb:input id="checkboxElt1340876414099dIi15v_checkbox" type="checkbox" name="conti_opp" value="conti_opp" editrole=",semi_edit,admin,edit" title="Continuity of Operations plan " tabindex="17" viewrole=",read_only,semi_edit" errorcode=" Continuity of Operations plan" schemaname="CONTI_OPP" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340869942226dIi15v"></fb:input>
                        <label>
                        Continuity of Operations plan
                        </label>
                     </div>
                     <div>
                        <fb:input id="checkboxElt1340876548991dIi15v_checkbox" type="checkbox" name="diversity" value="diversity" editrole=",semi_edit,admin,edit" title="Diversity Policy " tabindex="18" viewrole=",read_only,semi_edit" errorcode=" Diversity Policy" schemaname="DIVERSITY" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340869942226dIi15v"></fb:input>
                        <label>
                        Diversity Policy
                        </label>
                     </div>
                     <div>
                        <fb:input id="checkboxElt1340876679401dIi15v_checkbox" type="checkbox" name="emergency_pol" value="emergency_pol" editrole=",semi_edit,admin,edit" title="Emergency Preparedness Policy " tabindex="19" viewrole=",read_only,semi_edit" errorcode=" Emergency Preparedness Policy" schemaname="EMERGENCY_POL" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340869942226dIi15v"></fb:input>
                        <label>
                        Emergency Preparedness Policy
                        </label>
                     </div>
                     <div>
                        <fb:input id="checkboxElt1340876820674dIi15v_checkbox" type="checkbox" name="whistle_blower" value="whistle_blower" editrole=",semi_edit,admin,edit" title="Whistleblower Policy " tabindex="20" viewrole=",read_only,semi_edit" errorcode=" Whistleblower Policy" schemaname="WHISTLE_BLOWER" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340869942226dIi15v"></fb:input>
                        <label>
                        Whistleblower Policy
                        </label>
                     </div>
                     <div>
                        <fb:input id="checkboxElt1340876954169dIi15v_checkbox" type="checkbox" name="record_rettention" value="record_rettention" editrole=",semi_edit,admin,edit" title="Record Retention Policy " tabindex="21" viewrole=",read_only,semi_edit" errorcode=" Record Retention Policy" schemaname="RECORD_RETTEN" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340869942226dIi15v"></fb:input>
                        <label>
                        Record Retention Policy
                        </label>
                     </div>
                     <div>
                        <fb:input id="checkboxElt1340877117912dIi15v_checkbox" type="checkbox" name="security_pol" value="security_pol" editrole=",semi_edit,admin,edit" title="Security Policy " tabindex="22" viewrole=",read_only,semi_edit" errorcode=" Security Policy" schemaname="SECURITY_POL" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340869942226dIi15v"></fb:input>
                        <label>
                        Security Policy
                        </label>
                     </div>
                     <div>
                        <fb:input id="checkboxElt1340877370937dIi15v_checkbox" type="checkbox" name="transition" value="transition" editrole=",semi_edit,admin,edit" title="Succession/Transition Policy " tabindex="23" viewrole=",read_only,semi_edit" errorcode=" Succession/Transition Policy" schemaname="TRANSITION" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340869942226dIi15v"></fb:input>
                        <label>
                        Succession/Transition Policy
                        </label>
                     </div>
                     <div>
                        <fb:input id="checkboxElt1340877540511dIi15v_checkbox" type="checkbox" name="policy_none" value="policy_none" editrole=",semi_edit,admin,edit" title="None of the above " tabindex="24" viewrole=",read_only,semi_edit" errorcode=" None of the above" schemaname="POLICY_NONE" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340869942226dIi15v"></fb:input>
                        <label>
                        None of the above
                        </label>
                     </div>
                  </span>
                  <span class="formfield error">
                     <fb:error id="CEO_POLICY" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck29" class="class_185"></div>
         <div id="fb_divBl0ck30" class="class_186">
            <fb:line id="lineElt11340878137241dIi15v" name="div_row_q12" visibilityvalue="visibilityvalue" condition="condition" lineid="undefined" sectionname=" " y2="1179" y1="1120" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340879351948dIi15v_label" title="Please confirm that your organization has a written equal employment opportunity (EEO) policy" tag="label" for="dropdownElt1340879204632dIi15v_select" class="class_188">Please confirm that your organization has a written equal employment opportunity (EEO) policy:</label>
                  </span>
                  <span class="formfield">
                     <fb:select id="dropdownElt1340879204632dIi15v_select" name="eec_policy" cssclass="class_191" optionsize="3" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="Please confirm that your organization has a written equal employment opportunity (EEO) policy" events="eec_policy" tabindex="25" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="EEO_POLICY" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt11340878137241dIi15v">
                        <option value=" " title=" " selected="selected">Select</option>
                        <option value="Yes" title="Yes">Yes</option>
                        <option value="No" title="No">No</option>
                     </fb:select>
                  </span>
                  <span class="formfield error">
                     <fb:error id="EEO_POLICY" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck31" class="class_193"></div>
         <div id="fb_divBl0ck32" class="class_194">
            <fb:line id="lineElt11340879158183dIi15v" name="div_row_q13" visibilityvalue="visibilityvalue" condition="condition" lineid="undefined" sectionname=" " y2="1241" y1="1180" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340879640010dIi15v_label" title="How often does your organization create professional development plans for staff?" tag="label" for="dropdownElt1340879460508dIi15v_select" class="class_196">How often does your organization create professional development plans for staff?</label>
                  </span>
                  <span class="formfield">
                     <fb:select id="dropdownElt1340879460508dIi15v_select" name="professional_dev" cssclass="class_199" optionsize="3" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="Professional development plans are often linked to performance evaluations and consist of detailed objectives and actions. They are designed to promote a staff member's ability to most efficiently complete his/her responsibilities and prepare for additional career opportunities" events="professional_dev" tabindex="26" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="PROFESSIONAL_DEV" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt11340879158183dIi15v">
                        <option value=" " title=" " selected="selected">Select</option>
                        <option value="Annually" title="Annually">Annually</option>
                        <option value="Other" title="Other">Other</option>
                     </fb:select>
                  </span>
                  <span class="formfield error">
                     <fb:error id="PROFESSIONAL_DEV" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck33" class="class_201"></div>
         <div id="fb_divBl0ck34" class="class_202">
            <fb:line id="lineElt21340879439704dIi15v" name="div_row_q14" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="1352" y1="1242" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340879923430dIi15v_label" title="Please specify" tag="label" for="textareaElt1340879755121dIi15v_textarea" class="class_204">Please specify:</label>
                  </span>
                  <span class="formfield" maxlength="150">
                     <fb:textarea id="textareaElt1340879755121dIi15v_textarea" name="explain_profe_dev" cssclass=" input class_206" value="" editrole=",semi_edit,admin,edit" title="Please specify" tabindex="27" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="EXPLAIN_OTHER_DEVELOPMENT_PLAN" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340879439704dIi15v" cols="33" rows="2"></fb:textarea>
                  </span>
                  <span class="formfield error">
                     <fb:error id="EXPLAIN_OTHER_DEVELOPMENT_PLAN" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck35" class="class_209"></div>
         <div id="fb_divBl0ck36" class="class_210">
            <fb:line id="lineElt11340879718730dIi15v" name="div_row_q15" visibilityvalue="visibilityvalue" condition="condition" lineid="undefined" sectionname=" " y2="1412" y1="1353" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340880245316dIi15v_label" title="What is the minimum number of times a staff member is required to participate in training annually?" tag="label" for="dropdownElt1340880070553dIi15v_select" class="class_212">What is the minimum number of times a staff member is required to participate in training annually?</label>
                  </span>
                  <span class="formfield">
                     <fb:select id="dropdownElt1340880070553dIi15v_select" name="min_training_annual" cssclass="class_215" optionsize="17" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="Staff member training may include orientation for new employees, certification or licensing programs, training on policies and procedures, or other types of formal instruction for staff development." events="min_training_annual" tabindex="28" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="MIN_TRAINING_ANNUAL" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt11340879718730dIi15v">
                        <option value=" " title=" " selected="selected">Select</option>
                        <option value="0" title="0">0</option>
                        <option value="1" title="1">1</option>
                        <option value="2" title="2">2</option>
                        <option value="3" title="3">3</option>
                        <option value="4" title="4">4</option>
                        <option value="5" title="5">5</option>
                        <option value="6" title="6">6</option>
                        <option value="7" title="7">7</option>
                        <option value="8" title="8">8</option>
                        <option value="9" title="9">9</option>
                        <option value="10" title="10">10</option>
                        <option value="11" title="11">11</option>
                        <option value="12" title="12">12</option>
                        <option value="13" title="13">13</option>
                        <option value="14" title="14">14</option>
                        <option value="15" title="15">15</option>
                     </fb:select>
                  </span>
                  <span class="formfield error">
                     <fb:error id="MIN_TRAINING_ANNUAL" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck37" class="class_217"></div>
         <div id="fb_divBl0ck38" class="class_218">
            <fb:line id="lineElt11340880004358dIi15v" name="div_row_q16" visibilityvalue="visibilityvalue" condition="condition" lineid="undefined" sectionname=" " y2="1469" y1="1413" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340880525342dIi15v_label" title="How often does your organization conduct performance evaluations for staff?" tag="label" for="dropdownElt1340880371638dIi15v_select" class="class_220">How often does your organization conduct performance evaluations for staff?</label>
                  </span> 
                  <span class="formfield">
                     <fb:select id="dropdownElt1340880371638dIi15v_select" name="performance_evaluations" cssclass="class_223" optionsize="3" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="Performance evaluations consist of documented constructive feedback, which summarizes accomplishments, opportunities for improvement, and future objectives for staff members." events="performance_evaluations" tabindex="29" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="PERFORM_EVAL" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt11340880004358dIi15v">
                        <option value=" " title=" " selected="selected">Select</option>
                        <option value="Annually" title="Annually">Annually</option>
                        <option value="Other" title="Other">Other</option>
                     </fb:select>
                  </span>
                  <span class="formfield error">
                     <fb:error id="PERFORM_EVAL" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck39" class="class_225"></div>
         <div id="fb_divBl0ck40" class="class_226">
            <fb:line id="lineElt21340880341487dIi15v" name="div_row_q17" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="1578" y1="1470" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required" tag="span">*</span>
                  <label id="labelElt1340880899944dIi15v_label" title="Please specify" tag="label" for="textareaElt1340880650040dIi15v_textarea" class="class_228">Please specify:</label>
                  </span>
                  <span class="formfield" maxlength="150">
                     <fb:textarea id="textareaElt1340880650040dIi15v_textarea" name="eval_explain" cssclass=" input class_230" value="" editrole=",semi_edit,admin,edit" title="Please specify" tabindex="28" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="OTHER_PERFORM_EVAL" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340880341487dIi15v" cols="33" rows="2"></fb:textarea>
                  </span>
                  <span class="formfield error">
                     <fb:error id="OTHER_PERFORM_EVAL" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck41" class="class_233"></div>
         <div id="fb_divBl0ck42" class="class_234">
            <fb:line id="lineElt11340880608638dIi15v" name="div_row_q18" visibilityvalue="visibilityvalue" condition="condition" lineid="undefined" sectionname=" " y2="1643" y1="1579" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340881175518dIi15v_label" title="How often does your organization's senior management team formally review your organization's overall performance data?" tag="label" for="dropdownElt1340881017696dIi15v_select" class="class_236">How often does your organization's senior management team formally review your organization's overall performance data?</label>
                  </span> 
                  <span class="formfield">
                     <fb:select id="dropdownElt1340881017696dIi15v_select" name="overall_performance" cssclass="class_239" optionsize="7" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="Performance data includes key metrics to measure productivity and evaluate program outcomes that are critical to organization mission. Review of performance data often leads to documented evaluation of the current state of operations and strategic planning for future success." events="overall_performance" tabindex="30" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="OVERALL_PERFORMANCE" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt11340880608638dIi15v">
                        <option value=" " title=" " selected="selected">Select</option>
                        <option value="Weekly" title="Weekly">Weekly</option>
                        <option value="Monthly" title="Monthly">Monthly</option>
                        <option value="Quarterly" title="Quarterly">Quarterly</option>
                        <option value="Annually" title="Annually">Annually</option>
                        <option value="Other" title="Other">Other</option>
                        <option value="Never" title="Never">Never</option>
                     </fb:select>
                  </span>
                  <span class="formfield error">
                     <fb:error id="OVERALL_PERFORMANCE" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck43" class="class_241"></div>
         <div id="fb_divBl0ck44" class="class_242">
            <fb:line id="lineElt11340880984887dIi15v" name="div_row_q19" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="1754" y1="1644" x2="1670" x1="0">
               <div class="row">
                  <span class="label">
                  <span class="required">*</span>
                  <label id="labelElt1340881442004dIi15v_label" title="How often does your organization's senior management team formally review your organization's overall performance data?" tag="label" for="textareaElt1340881304243dIi15v_textarea" class="class_244">Please explain:</label>
                  </span>
                  <span class="formfield" maxlength="150">
                     <fb:textarea id="textareaElt1340881304243dIi15v_textarea" name="annual_per_explain" cssclass=" input class_246" value="" editrole=",semi_edit,admin,edit" title="Please explain in brief." tabindex="31" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="OTHER_OVERALL_PERFORMANCE" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt11340880984887dIi15v" cols="33" rows="2"></fb:textarea>
                  </span>
                  <span class="formfield error">
                     <fb:error id="OTHER_OVERALL_PERFORMANCE" cssclass="error"></fb:error>
                  </span>
               </div>
            </fb:line>
         </div>
         <div id="fb_divBl0ck45" class="class_249"></div>
         <div id="fb_divBl0ck46" class="class_250">
            <fb:line id="lineElt11340881502925dIi15v" name="div_row_q20" visibilityvalue="Visible" condition="condition" lineid="undefined" sectionname=" " y2="1796" y1="1755" x2="1670" x1="0">
               <span class="buttonholder">
                  <fb:button id="buttonElt11340881502927dIi15v_button" type="button" name="back" cssclass=" button graybtutton class_252" value=" &lt;&lt; Back " editrole=",semi_edit,admin,edit" title="Back" events="eventname" tabindex="34" viewrole=",read_only,semi_edit" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" lineid="lineElt11340881502925dIi15v" defaultpermission="view"></fb:button>
                  <fb:button id="buttonElt11341727695460dIi15v_button" type="button" name="save" cssclass=" button class_255" value=" Save " editrole=",semi_edit,admin,edit" title="Save" events="eventname" tabindex="35" viewrole=",read_only,semi_edit" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" lineid="lineElt11340881502925dIi15v" defaultpermission="view"></fb:button>
                  <fb:button id="buttonElt21341728063174dIi15v_button" type="button" name="save_next" cssclass=" button class_258" value=" Save &amp; Next " editrole=",semi_edit,admin,edit" title="Save &amp; Next" events="eventname" tabindex="36" viewrole=",read_only,semi_edit" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" lineid="lineElt11340881502925dIi15v" defaultpermission="view"></fb:button>
               </span>
            </fb:line>
         </div>
         <div id="fb_divBl0ck47" class="class_260"></div>
         <fb:input id="fbjscounter" type="hidden" name="fbjscounter" value="1"></fb:input>
      </form>
   </div>
</body>
</html>
