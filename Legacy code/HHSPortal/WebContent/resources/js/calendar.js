/**
 * Calendar js
 */

var winCal;
var dtToday;
var Cal;
var MonthName;
var WeekDayName1;
var WeekDayName2;
var exDateTime;
var selDate;
var calSpanID = "calBorder";  
var domStyle=null;  
var cnLeft="0";
var cnTop="0";
var xpos=0; 
var ypos=0; 
var calHeight=0; 
var CalWidth=208;
var CellWidth=30;
var TimeMode=24;
var StartYear =1800; 
var EndYear = 50; 






var SpanBorderColor = "#DDDDDD"; 
var SpanBgColor = "#FFFFFF";
var WeekChar=2;
var DateSeparator="/";
var ShowLongMonth=true;
var ShowMonthYear=true;
var MonthYearColor="#cc0033";
var WeekHeadColor="#E3F1F8";
var SundayColor="#E3F1F8";
var SaturdayColor="#E3F1F8";
var WeekDayColor="white";
var FontColor="blue";
var TodayColor="#4297E2";
var SelDateColor = "#8DD53C";
var YrSelColor="#cc0033";
var MthSelColor="#cc0033";
var HoverColor="#C5DFEC"; 
var ThemeBg="";
var CalBgColor="";
var PrecedeZero=true;
var MondayFirstDay=true;
var UseImageFiles = true;



var MonthName=["January", "February", "March", "April", "May", "June","July","August", "September", "October", "November", "December"];
var WeekDayName1=["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"];
var WeekDayName2=["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"];

/**
 * Binds calendar pop up to calendar image
 */
function NewCssCal(pCtrl,event,pFormat,pScroller,pShowTime,pTimeMode,pHideSeconds) {
	pickIt(event);
	
	dtToday = new Date();
	Cal=new Calendar(dtToday);
	if ((pShowTime!=null) && (pShowTime)) {
		Cal.ShowTime=true;
		if ((pTimeMode!=null) &&((pTimeMode=='12')||(pTimeMode=='24')))   {
			TimeMode=pTimeMode;
		}
		else TimeMode='24';
		if (pHideSeconds!=null)
		{
			if (pHideSeconds)
			{
				Cal.ShowSeconds=false;
			}
			else
			{
				Cal.ShowSeconds=true;
			}
		}
		else
		{
			Cal.ShowSeconds=false;
		}    
	}
	if (pCtrl!=null)
		Cal.Ctrl=pCtrl;
	if (pFormat!=null)
		Cal.Format=pFormat.toUpperCase();
	else 
		Cal.Format="MMDDYYYY";
	if (pScroller!=null) {
		if (pScroller.toUpperCase()=="ARROW") {
			Cal.Scroller="ARROW";
		}
		else {
			Cal.Scroller="DROPDOWN";
		}
	}
	exDateTime=document.getElementById(pCtrl).value;
	if (exDateTime!="")     { 
		var Sp1;
		var Sp2; 
		var tSp1;
		var tSp1;
		var strMonth="";
		var strDate="";
		var strYear="";
		var intMonth;
		var YearPattern;
		var strHour;
		var strMinute;
		var strSecond;
		var winHeight;
		//parse month
		Sp1=exDateTime.indexOf(DateSeparator,0);
		Sp2=exDateTime.indexOf(DateSeparator,(parseInt(Sp1)+1));
		var offset=parseInt(Cal.Format.toUpperCase().lastIndexOf("M"))-parseInt(Cal.Format.toUpperCase().indexOf("M"))-1;
		if ((Cal.Format.toUpperCase()=="DDMMYYYY") || (Cal.Format.toUpperCase()=="DDMMMYYYY")) {

			if (DateSeparator=="") {

				strMonth=exDateTime.substring(2,4+offset);

				strDate=exDateTime.substring(0,2);

				strYear=exDateTime.substring(4+offset,8+offset);

			}

			else {

				if(exDateTime.indexOf("D*") != -1) {   

					strMonth = exDateTime.substring(8, 11);

					strDate  = exDateTime.substring(0, 2);

					strYear  = "20" + exDateTime.substring(11, 13);  

				} else {

					if((Sp1 != '-1') || (Sp2 != '-1')){

						strMonth=exDateTime.substring(Sp1+1,Sp2);

						strDate=exDateTime.substring(0,Sp1);

						strYear=exDateTime.substring(Sp2+1,Sp2+5);


					}

				}

			}



		}



		else if ((Cal.Format.toUpperCase()=="MMDDYYYY") || (Cal.Format.toUpperCase()=="MMMDDYYYY")) {



			if (DateSeparator=="") {

				strMonth=exDateTime.substring(0,2+offset);

				strDate=exDateTime.substring(2+offset,4+offset);

				strYear=exDateTime.substring(4+offset,8+offset);

			}



			else {

				if((Sp1 != '-1') || (Sp2 != '-1')){

					strMonth=exDateTime.substring(0,Sp1);

					strDate=exDateTime.substring(Sp1+1,Sp2);

					strYear=exDateTime.substring(Sp2+1,Sp2+5);

				}

			}



		}
		else if ((Cal.Format.toUpperCase()=="YYYYMMDD") || (Cal.Format.toUpperCase()=="YYYYMMMDD")) {
			if (DateSeparator=="") {
				strMonth=exDateTime.substring(4,6+offset);
				strDate=exDateTime.substring(6+offset,8+offset);
				strYear=exDateTime.substring(0,4);
			}
			else {
				
				if((Sp1 != '-1') || (Sp2 != '-1')){
					strMonth=exDateTime.substring(Sp1+1,Sp2);
					strDate=exDateTime.substring(Sp2+1,Sp2+3);
					strYear=exDateTime.substring(0,Sp1);
				
				}
			}
		}



		else if ((Cal.Format.toUpperCase()=="YYMMDD") || (Cal.Format.toUpperCase()=="YYMMMDD")) {



			if (DateSeparator=="") {

				strMonth=exDateTime.substring(2,4+offset);

				strDate=exDateTime.substring(4+offset,6+offset);

				strYear=exDateTime.substring(0,2);

			}



			else {

				strMonth=exDateTime.substring(Sp1+1,Sp2);

				strDate=exDateTime.substring(Sp2+1,Sp2+3);

				strYear=exDateTime.substring(0,Sp1);

			}



		}           


		if(parseInt(strYear) <= StartYear)
			strYear = StartYear;
		if(parseInt(strYear) >= dtToday.getFullYear() + EndYear)
			strYear = dtToday.getFullYear() + EndYear;
		
		
		if (isNaN(strMonth))

			intMonth=Cal.GetMonthIndex(strMonth);

		else

			intMonth=parseInt(strMonth,10)-1;   



		if ((parseInt(intMonth,10)>=0) && (parseInt(intMonth,10)<12))

			Cal.Month=intMonth;


		if ((parseInt(strDate,10)<=Cal.GetMonDays()) && (parseInt(strDate,10)>=1))

			Cal.Date=strDate;

		YearPattern=/^\d{4}$/;

		if (YearPattern.test(strYear))

			Cal.Year=parseInt(strYear,10);

		if (Cal.ShowTime==true) {

			if (TimeMode==12) {

				strAMPM=exDateTime.substring(exDateTime.length-2,exDateTime.length);

				Cal.AMorPM=strAMPM;

			}

			tSp1=exDateTime.indexOf(":",0);

			tSp2=exDateTime.indexOf(":",(parseInt(tSp1)+1));

			if (tSp1>0) {

				strHour=exDateTime.substring(tSp1,(tSp1)-2);

				Cal.SetHour(strHour);

				strMinute=exDateTime.substring(tSp1+1,tSp1+3);

				Cal.SetMinute(strMinute);

				strSecond=exDateTime.substring(tSp2+1,tSp2+3);

				Cal.SetSecond(strSecond);

			} else if(exDateTime.indexOf("D*") != -1) {  

				strHour = exDateTime.substring(2, 4);
				Cal.SetHour(strHour);
				strMinute = exDateTime.substring(4, 6);
				Cal.SetMinute(strMinute);
			}
		}     
	}
	selDate=new Date(Cal.Year,Cal.Month,Cal.Date);
	RenderCssCal(true);
}

/**
 * This function renders calendar pop up
 */

function RenderCssCal(bNewCal) {

	if (typeof bNewCal == "undefined" || bNewCal != true) {bNewCal = false;}
	var vCalHeader;
	var vCalData;
	var vCalTime="";

	var i;
	var j;

	var SelectStr;
	var vDayCount=0;
	var vFirstDay;

	calHeight = 0; 

	winCalData="<span style='cursor:auto;'>\n";

	if (ThemeBg==""){CalBgColor="bgcolor='"+WeekDayColor+"'";}
	vCalHeader="<table "+CalBgColor+" background='"+ThemeBg+"' class='calendarMainBg'  border=1 cellpadding=1 cellspacing=1 width='200px' valign='top' >\n";

	vCalHeader+="<tr>\n<td colspan='7' class='calendarTopTable'>\n<table border='0' class='calendarMainBg' width='200px' cellpadding='0' cellspacing='0'>\n<tr>\n";

	if (Cal.Scroller=="DROPDOWN") {
		vCalHeader+="<td align='center'><select name=\"MonthSelector\" onChange=\"javascript:Cal.SwitchMth(this.selectedIndex);RenderCssCal();\">\n";
		for (i=0;i<12;i++) {
			if (i==Cal.Month)
				SelectStr="Selected";
			else
				SelectStr="";
			vCalHeader+="<option "+SelectStr+" value="+i+">"+MonthName[i]+"</option>\n";

		}

		vCalHeader+="</select></td>\n";
		

		vCalHeader+="<td align='center'><select name=\"YearSelector\" size=\"1\" onChange=\"javascript:Cal.SwitchYear(this.value);RenderCssCal();\">\n";
		for (i = StartYear; i <= (dtToday.getFullYear() + EndYear);i++)   {
			
			if (i==Cal.Year)
				SelectStr="Selected";
			else
				SelectStr="";	
			vCalHeader+="<option "+SelectStr+" value="+i+">"+i+"</option>\n";

		}

		vCalHeader+="</select></td><td><label style=\"color:white; cursor:pointer;\"  title=\"Close\" onclick=\"javascript:closePopUp();\">X</label></td>\n";
		calHeight += 30;
	}

	else if (Cal.Scroller=="ARROW")	
	{	

		if (UseImageFiles)
		{
			vCalHeader+="<td><img onmousedown='javascript:Cal.DecYear();RenderCssCal();' src='images/cal_fastreverse.gif' width='13px' height='9' onmouseover='changeBorder(this, 0)' onmouseout='changeBorder(this, 1)' style='border:1px solid white'></td>\n";
			vCalHeader+="<td><img onmousedown='javascript:Cal.DecMonth();RenderCssCal();' src='images/cal_reverse.gif' width='13px' height='9' onmouseover='changeBorder(this, 0)' onmouseout='changeBorder(this, 1)' style='border:1px solid white'></td>\n";
			vCalHeader+="<td width='70%' class='calR'><font color='"+YrSelColor+"'>"+Cal.GetMonthName(ShowLongMonth)+" "+Cal.Year+"</font></td>\n";
			vCalHeader+="<td><img onmousedown='javascript:Cal.IncMonth();RenderCssCal();' src='images/cal_forward.gif' width='13px' height='9' onmouseover='changeBorder(this, 0)' onmouseout='changeBorder(this, 1)' style='border:1px solid white'></td>\n";
			vCalHeader+="<td><img onmousedown='javascript:Cal.IncYear();RenderCssCal();' src='images/cal_fastforward.gif' width='13px' height='9' onmouseover='changeBorder(this, 0)' onmouseout='changeBorder(this, 1)' style='border:1px solid white'></td>\n";

			calHeight += 22;
		}
		else
		{
			vCalHeader+="<td><span id='dec_year' title='reverse year' onmousedown='javascript:Cal.DecYear();RenderCssCal();' onmouseover='changeBorder(this, 0)' onmouseout='changeBorder(this, 1)' style='border:1px solid white; color:"+YrSelColor+"'>-</span></td>";
			vCalHeader+="<td><span id='dec_month' title='reverse month' onmousedown='javascript:Cal.DecMonth();RenderCssCal();' onmouseover='changeBorder(this, 0)' onmouseout='changeBorder(this, 1)' style='border:1px solid white'>&lt;</span></td>\n";
			vCalHeader+="<td width='70%' class='calR'><font color='"+YrSelColor+"'>"+Cal.GetMonthName(ShowLongMonth)+" "+Cal.Year+"</font></td>\n";
			vCalHeader+="<td><span id='inc_month' title='forward month' onmousedown='javascript:Cal.IncMonth();RenderCssCal();' onmouseover='changeBorder(this, 0)' onmouseout='changeBorder(this, 1)' style='border:1px solid white'>&gt;</span></td>\n";
			vCalHeader+="<td><span id='inc_year' title='forward year' onmousedown='javascript:Cal.IncYear();RenderCssCal();'  onmouseover='changeBorder(this, 0)' onmouseout='changeBorder(this, 1)' style='border:1px solid white; color:"+YrSelColor+"'>+</span></td>\n";
			calHeight += 22;
		}
	}

	vCalHeader+="</tr>\n</table>\n</td>\n</tr>\n";

	if ((ShowMonthYear)&&(Cal.Scroller=="DROPDOWN")) {
		vCalHeader+="<tr><td colspan='7' class='calendarMonthBg' class='calR'>\n"+Cal.GetMonthName(ShowLongMonth)+" "+Cal.Year+"\n</td></tr>\n";
		calHeight += 19;
	}

	vCalHeader+="<tr><td colspan=\"7\" ><table cellspacing=1 class='calendarMainBg'><tr>\n";
	var WeekDayName=new Array();
	if (MondayFirstDay==true)
		WeekDayName=WeekDayName2;
	else
		WeekDayName=WeekDayName1;
	for (i=0;i<7;i++) {
		vCalHeader+="<td style='background:#C5DFEC; font-weight:bold;' bgcolor="+WeekHeadColor+" width='"+CellWidth+"px' class='calTD' >"+WeekDayName[i].substr(0,WeekChar)+"</td>\n";
	}

	calHeight += 19;
	vCalHeader+="</tr>\n";	

	CalDate=new Date(Cal.Year,Cal.Month);
	CalDate.setDate(1);

	vFirstDay=CalDate.getDay();


	if (MondayFirstDay==true) {
		vFirstDay-=1;
		if (vFirstDay==-1)
			vFirstDay=6;
	}


	vCalData="<tr>";
	calHeight += 19;
	for (i=0;i<vFirstDay;i++) {
		vCalData=vCalData+GenCell();
		vDayCount=vDayCount+1;
	}

	for (j=1;j<=Cal.GetMonDays();j++) {
		var strCell;
		if((vDayCount%7==0)&&(j > 1)) {
			vCalData=vCalData+"\n<tr>";
		}

		vDayCount=vDayCount+1;
		if ((j==dtToday.getDate())&&(Cal.Month==dtToday.getMonth())&&(Cal.Year==dtToday.getFullYear()))
			strCell=GenCell(j,true,TodayColor);
		else {
			if ((j==selDate.getDate())&&(Cal.Month==selDate.getMonth())&&(Cal.Year==selDate.getFullYear())) { 
				strCell=GenCell(j,true,SelDateColor);
			}
			else {	
				if (MondayFirstDay==true) {
					if (vDayCount%7==0)
						strCell=GenCell(j,false,SundayColor);
					else if ((vDayCount+1)%7==0)
						strCell=GenCell(j,false,SaturdayColor);
					else
						strCell=GenCell(j,null,WeekDayColor);					
				} 
				else {
					if (vDayCount%7==0)
						strCell=GenCell(j,false,SaturdayColor);
					else if ((vDayCount+6)%7==0)
						strCell=GenCell(j,false,SundayColor);
					else
						strCell=GenCell(j,null,WeekDayColor);
				}
			}		
		}						

		vCalData=vCalData+strCell;

		if((vDayCount%7==0)&&(j<Cal.GetMonDays())) {
			vCalData=vCalData+"\n</tr>";
			calHeight += 19;
		}
	}

	if(!(vDayCount%7) == 0) {
		while(!(vDayCount % 7) == 0) {
			vCalData=vCalData+GenCell();
			vDayCount=vDayCount+1;
		}
	}

	vCalData=vCalData+"\n</table></td></tr>";

	if (Cal.ShowTime) 
	{
		var showHour;
		var ShowArrows=false;
		var HourCellWidth="35px";
		showHour=Cal.getShowHour();

		if (Cal.ShowSeconds==false && TimeMode==24 ) 
		{
			ShowArrows=true;
			HourCellWidth="10px";
		}

		vCalTime="\n<tr>\n<td colspan='7' align='center'><center>\n<table border='0' class='calendarMainBg' width='199px' cellpadding='0' cellspacing='2'>\n<tr>\n<td height='5px' width='"+HourCellWidth+"px'>&nbsp;</td>\n";

		if (ShowArrows && UseImageFiles) 
		{   
			vCalTime+="<td align='center' style='background:#E4E4E4;'><table cellspacing='0' cellpadding='0' style='line-height:0pt'><tr><td><img onmousedown='startSpin(\"Hour\", \"plus\");' onmouseup='stopSpin();' src='images/cal_plus.gif' width='13px' height='9px' onmouseover='changeBorder(this, 0)' onmouseout='changeBorder(this, 1)' style='border:1px solid white;'></td></tr><tr><td><img onmousedown='startSpin(\"Hour\", \"minus\");' onmouseup='stopSpin();' src='images/cal_minus.gif' width='13px' height='9px' onmouseover='changeBorder(this, 0)' onmouseout='changeBorder(this, 1)' style='border:1px solid white'></td></tr></table></td>\n"; 
		}

		vCalTime+="<td align='center' width='22px'><input type='text' name='hour' maxlength=2 size=1 style=\"WIDTH:22px\" value="+showHour+" onChange=\"javascript:Cal.SetHour(this.value)\">";
		vCalTime+="</td><td align='center' style='font-size:150%; font-weight:bold;'>:</td><td align='center' width='22px' >";
		vCalTime+="<input type='text' name='minute' maxlength=2 size=1 style=\"WIDTH: 22px\" value="+Cal.Minutes+" onChange=\"javascript:Cal.SetMinute(this.value)\">";

		if (Cal.ShowSeconds) {
			vCalTime+="</td><td align='center'>:</td><td align='center' width='22px'>";
			vCalTime+="<input type='text' name='second' maxlength=2 size=1 style=\"WIDTH: 22px\" value="+Cal.Seconds+" onChange=\"javascript:Cal.SetSecond(parseInt(this.value,10))\">";
		}

		if (TimeMode==12) {
			var SelectAm =(Cal.AMorPM=="AM")? "Selected":"";
			var SelectPm =(Cal.AMorPM=="PM")? "Selected":"";

			vCalTime+="</td><td>";
			vCalTime+="<select name=\"ampm\" onChange=\"javascript:Cal.SetAmPm(this.options[this.selectedIndex].value);\">\n";
			vCalTime+="<option "+SelectAm+" value=\"AM\">AM</option>";
			vCalTime+="<option "+SelectPm+" value=\"PM\">PM<option>";
			vCalTime+="</select>";
		}

		if (ShowArrows && UseImageFiles) {
			vCalTime+="</td>\n<td align='center'><table cellspacing='0' cellpadding='0' style='line-height:0pt'><tr><td><img onmousedown='startSpin(\"Minute\", \"plus\");' onmouseup='stopSpin();' src='images/cal_plus.gif' width='13px' height='9px' onmouseover='changeBorder(this, 0)' onmouseout='changeBorder(this, 1)' style='border:1px solid white'></td></tr><tr><td><img onmousedown='startSpin(\"Minute\", \"minus\");' onmouseup='stopSpin();' src='images/cal_minus.gif' width='13px' height='9px' onmouseover='changeBorder(this, 0)' onmouseout='changeBorder(this, 1)' style='border:1px solid white'></td></tr></table>"; 
		}

		vCalTime+="</td>\n<td align='right' valign='bottom' width='"+HourCellWidth+"px'>";
	}

	else
	{vCalTime+="\n<tr>\n<td colspan='7' align='right'>";}

	if (UseImageFiles)
	{
		vCalTime+="<img onmousedown='javascript:closewin(\"" + Cal.Ctrl + "\"); stopSpin();' src='images/cal_close.gif' width='16px' height='14px' onmouseover='changeBorder(this,0)' onmouseout='changeBorder(this, 1)' style='border:1px solid white'></td>";
	}
	else
	{
		vCalTime+="<span id='close_cal' title='close'onmousedown='javascript:closewin(\"" + Cal.Ctrl + "\");' onmouseover='changeBorder(this, 0)'onmouseout='changeBorder(this, 1)' style='border:1px solid white; font-family: Arial;font-size: 10pt;'>x</span></td>";
	}

	vCalTime+="</tr>\n</table></center>\n</td>\n</tr>";

	vCalTime+="\n</table>\n</span>";

	var CalenId = document.getElementById(Cal.Ctrl); 
	
	var funcCalback="function callback(id, datum) {\n";
	funcCalback+=" var CalId = document.getElementById(id); var previousValue=CalId.value;if (datum== 'undefined') { var d = new Date(); datum = d.getDate() + '/' +(d.getMonth()+1) + '/' + d.getFullYear(); } window.calDatum=datum;CalId.value=datum;\n";
	funcCalback+=" if (Cal.ShowTime) {\n";
	funcCalback+=" CalId.value+=' '+Cal.getShowHour()+':'+Cal.Minutes;\n";
	funcCalback+=" if (Cal.ShowSeconds)\n  CalId.value+=':'+Cal.Seconds;\n";
	funcCalback+=" if (TimeMode==12)\n  CalId.value+=''+Cal.getShowAMorPM();\n";
	if(detectBrowser() == "ie" && Browser.Version() < 8)
	{
		funcCalback+="}\n winCal.style.visibility='hidden';CalId.focus();if(CalId.copiedEvent != undefined){var functionEvent;var tempCalId= id.split('_calendar');var flag=\"true\";if(!isNaN(parseInt(tempCalId[1]))){functionEvent= CalId.copiedEvent.split(':');}else{flag=\"false\";}if((previousValue!=CalId.value) && (flag == \"true\")){var onchangefkn= new Function(functionEvent[1]); onchangefkn();}else if(flag == \"false\"){CalId.onchange();}\n}else{if(previousValue!=CalId.value){if(CalId.onchange != null){CalId.onchange();}}}}\n";
	}
	else
	{funcCalback+="}\n winCal.style.visibility='hidden';CalId.focus();var functionEvent;var tempCalId= id.split('_calendar');if(previousValue!=CalId.value){if(CalId.onchange != null){CalId.onchange();}}\n}\n";
	}
	funcCalback+="  function reset(id){";
	funcCalback+=" 	document.getElementById(id).value=''; winCal.style.visibility='hidden';";
	funcCalback+="  }";
	funcCalback+="  function closePopUp(){";
	funcCalback+=" 	winCal.style.visibility='hidden';";
	funcCalback+="  }";


	if((f_clientWidth()- xpos) < CalWidth)
	{xpos = xpos - 322;
	}

	var clientHeight= (document.documentElement.clientHeight >0) ? document.documentElement.clientHeight :f_clientHeight();
	
	if((clientHeight - ypos) < calHeight)
	{     
		ypos = ypos - calHeight ;
	}

	if (winCal == undefined) {
		var headID = document.getElementsByTagName("head")[0];

		
		var e = document.createElement("script");
		e.type = "text/javascript";
		e.language = "javascript";
		e.text = funcCalback;
		headID.appendChild(e);
		

		var cssStr = ".calTD {font-family: verdana; font-size: 12px; text-align: center; background:#E3F1F8; border:0;  }\n";
		
		cssStr+= ".calR {font-family: verdana; font-size: 12px; text-align: center; font-weight: bold; }";

		var style = document.createElement("style");
		style.type = "text/css";
		style.rel = "stylesheet";
		if(style.styleSheet) {
			style.styleSheet.cssText = cssStr;
		} 

		else { 
			var cssText = document.createTextNode(cssStr);
			style.appendChild(cssText);
		}

		headID.appendChild(style);
	
		var span = document.createElement("span");
		span.id = calSpanID;

		with (span.style) {position = "absolute"; left = (xpos+8)+'px'; top = (ypos-8)+'px'; width = CalWidth+'px'; border = "solid 1px " + SpanBorderColor; padding = "4px"; cursor = "default"; backgroundColor = SpanBgColor; zIndex = 100;}
		document.body.appendChild(span);
		winCal=document.getElementById(calSpanID);
	}

	else {
		winCal.style.visibility = "visible";
		winCal.style.Height = calHeight;

		
		if(bNewCal==true){
			winCal.style.left = (xpos+8)+'px';
			winCal.style.top = (ypos-8)+'px';
		}
	}
	winCal.style.zIndex="2147483647";
	winCal.innerHTML=winCalData + vCalHeader + vCalData+"<tr style='width=2px'><td><input type='button' class='calendarResetBtn' onclick='javascript:reset(\""+Cal.Ctrl+"\");clearSpan(\""+Cal.Ctrl+"\");' value='Reset Date' name='Reset Date'></input></tr></td>";// + vCalTime;
	return true;
}

/**
 * This function remove error message
 */
function clearSpan(id){
	if(typeof document.getElementById(id+'Error') != 'undefined'){
		if(document.getElementById(id+'Error') != null){
			if($('#'+id+'Error').html() != null){
				$('#'+id+'Error').html('');
			}
		}
	}
}

/**
 * This function gets client browser height
 */
function f_clientHeight() {
	return f_filterResults (
			window.innerHeight ? window.innerHeight : 0,
					document.documentElement ? document.documentElement.clientHeight : 0,
							document.body ? document.body.clientHeight : 0
	);
}

/**
 * This function gets client browser width
 */
function f_clientWidth() {
	return f_filterResults (
			window.innerWidth ? window.innerWidth : 0,
					document.documentElement ? document.documentElement.clientWidth : 0,
							document.body ? document.body.clientWidth : 0
	);
}

/**
 * This function filters the result
 */
function f_filterResults(n_win, n_docel, n_body) {
	var n_result = n_win ? n_win : 0;
	if (n_docel && (!n_result || (n_result > n_docel)))
		n_result = n_docel;
	return n_body && (!n_result || (n_result > n_body)) ? n_body : n_result;
} 

/**
 * This function  generates cell of calendar
 */
function GenCell(pValue,pHighLight,pColor) { 
	var PValue;
	var PCellStr;
	var vColor;

	var vHLstr1;
	var vHlstr2;
	var vTimeStr;

	if (pValue==null)
		PValue="";
	else
		PValue=pValue;
	if (pColor!=null)
		vColor="bgcolor=\""+pColor+"\"";
	else
		vColor=CalBgColor;
	if ((pHighLight!=null)&&(pHighLight)) {
		vHLstr1="<font class='calR'>";vHLstr2="</font>";
	}
	else {
		vHLstr1="";vHLstr2="";
	}

	if (Cal.ShowTime) {
		vTimeStr=' '+Cal.Hours+':'+Cal.Minutes;
		if (Cal.ShowSeconds)
			vTimeStr+=':'+Cal.Seconds;
		if (TimeMode==12)
			vTimeStr+=' '+Cal.AMorPM;
	}	

	else
		vTimeStr="";		

	if (PValue!="") {
	

		if(pColor == SaturdayColor || pColor == SundayColor || pColor == SelDateColor || pColor == TodayColor) {
			PCellStr="\n<td "+vColor+" class='calTD' style='cursor: pointer;' onmouseover='changeBorder(this, 0);' onmouseout=\"changeBorder(this, 1, '"+pColor+"');\" onClick=\"javascript:callback('"+Cal.Ctrl+"','"+Cal.FormatDate(PValue)+"');\">"+vHLstr1+PValue+vHLstr2+"</td>";

		}
		else {
			PCellStr="\n<td "+vColor+" class='calTD' style='cursor: pointer;' onmouseover='changeBorder(this, 0);' onmouseout='changeBorder(this, 1);' onClick=\"javascript:callback('"+Cal.Ctrl+"','"+Cal.FormatDate(PValue)+"');\">"+vHLstr1+PValue+vHLstr2+"</td>";
		}
	}
	else

		PCellStr="\n<td "+vColor+" class='calTD'>&nbsp;</td>";

	return PCellStr;

}


/**
 * This function  caledar type bean
 */
function Calendar(pDate,pCtrl) {


	this.Date=pDate.getDate();
	this.Month=pDate.getMonth();
	this.Year=pDate.getFullYear();
	this.Hours=pDate.getHours();

	if (pDate.getMinutes()<10)
		this.Minutes="0"+pDate.getMinutes();
	else
		this.Minutes=pDate.getMinutes();

	if (pDate.getSeconds()<10)
		this.Seconds="0"+pDate.getSeconds();
	else		
		this.Seconds=pDate.getSeconds();


	this.MyWindow=winCal;
	this.Ctrl=pCtrl;
	this.Format="ddMMyyyy";
	this.Separator=DateSeparator;
	this.ShowTime=false;
	this.Scroller="DROPDOWN";
	if (pDate.getHours()<12)
		this.AMorPM="AM";
	else
		this.AMorPM="PM";

	this.ShowSeconds=true;		
}


/**
 * This function gets current index
 */
function GetMonthIndex(shortMonthName) {
	for (i=0;i<12;i++) {
		if (MonthName[i].substring(0,3).toUpperCase()==shortMonthName.toUpperCase()) 
		{return i;}
	}
}

Calendar.prototype.GetMonthIndex=GetMonthIndex;
//increments year
function IncYear() {
	Cal.Year++;}
Calendar.prototype.IncYear=IncYear;
//decrements year
function DecYear() {
	Cal.Year--;}
Calendar.prototype.DecYear=DecYear;
//increments month
function IncMonth() {	
	Cal.Month++;
	if (Cal.Month>=12) {
		Cal.Month=0;
		Cal.IncYear();
	}
}

Calendar.prototype.IncMonth=IncMonth;
//decrements month
function DecMonth() {	
	Cal.Month--;
	if (Cal.Month<0) {
		Cal.Month=11;
		Cal.DecYear();
	}
}

Calendar.prototype.DecMonth=DecMonth;
// switches month
function SwitchMth(intMth) {
	Cal.Month=intMth;}
Calendar.prototype.SwitchMth=SwitchMth;
//switches year
function SwitchYear(intYear) {
	Cal.Year=intYear;}
Calendar.prototype.SwitchYear=SwitchYear;
// sets hour
function SetHour(intHour) {	
	var MaxHour;
	var MinHour;
	if (TimeMode==24) {
		MaxHour=23;MinHour=0;}
	else if (TimeMode==12) {
		MaxHour=12;MinHour=1;}

	var HourExp=new RegExp("^\\d\\d");
	var SingleDigit=new RegExp("\\d");

	if ((HourExp.test(intHour) || SingleDigit.test(intHour)) && (parseInt(intHour,10)>MaxHour)) {
		intHour = MinHour;
	}

	else if ((HourExp.test(intHour) || SingleDigit.test(intHour)) && (parseInt(intHour,10)<MinHour)) {
		intHour = MaxHour;
	}

	if (SingleDigit.test(intHour)) {
		intHour="0"+intHour+"";	
	}

	if (HourExp.test(intHour) && (parseInt(intHour,10)<=MaxHour) && (parseInt(intHour,10)>=MinHour)) {	
		if ((TimeMode==12) && (Cal.AMorPM=="PM")) {
			if (parseInt(intHour,10)==12)
				Cal.Hours=12;
			else	
				Cal.Hours=parseInt(intHour,10)+12;
		}	

		else if ((TimeMode==12) && (Cal.AMorPM=="AM")) {
			if (intHour==12)
				intHour-=12;

			Cal.Hours=parseInt(intHour,10);
		}

		else if (TimeMode==24)
			Cal.Hours=parseInt(intHour,10);	
	}

}

Calendar.prototype.SetHour=SetHour;
//sets minute
function SetMinute(intMin) {
	var MaxMin=59;
	var MinMin=0;

	var SingleDigit=new RegExp("\\d");
	var SingleDigit2=new RegExp("^\\d{1}$");
	var MinExp=new RegExp("^\\d{2}$");

	if ((MinExp.test(intMin) || SingleDigit.test(intMin)) && (parseInt(intMin,10)>MaxMin)) {
		intMin = MinMin;
	}

	else if ((MinExp.test(intMin) || SingleDigit.test(intMin)) && (parseInt(intMin,10)<MinMin))	{
		intMin = MaxMin;
	}

	var strMin = intMin + "";
	if (SingleDigit2.test(intMin)) {
		strMin="0"+strMin+"";
	} 

	if ((MinExp.test(intMin) || SingleDigit.test(intMin)) 
			&& (parseInt(intMin,10)<=59) && (parseInt(intMin,10)>=0)) {

		Cal.Minutes=strMin;
	}
}

Calendar.prototype.SetMinute=SetMinute;
//sets second
function SetSecond(intSec) {	
	var MaxSec=59;
	var MinSec=0;

	var SingleDigit=new RegExp("\\d");
	var SingleDigit2=new RegExp("^\\d{1}$");
	var SecExp=new RegExp("^\\d{2}$");

	if ((SecExp.test(intSec) || SingleDigit.test(intSec)) && (parseInt(intSec,10)>MaxSec)) {
		intSec = MinSec;
	}

	else if ((SecExp.test(intSec) || SingleDigit.test(intSec)) && (parseInt(intSec,10)<MinSec))	{
		intSec = MaxSec;
	}

	var strSec = intSec + "";
	if (SingleDigit2.test(intSec)) {
		strSec="0"+strSec+"";
	} 

	if ((SecExp.test(intSec) || SingleDigit.test(intSec)) 
			&& (parseInt(intSec,10)<=59) && (parseInt(intSec,10)>=0)) {

		Cal.Seconds=strSec;
	}

}

Calendar.prototype.SetSecond=SetSecond;
// sets time in AM PM
function SetAmPm(pvalue) {
	this.AMorPM=pvalue;
	if (pvalue=="PM") {

		this.Hours=(parseInt(this.Hours,10))+12;
		if (this.Hours==24)
			this.Hours=12;
	}	

	else if (pvalue=="AM")
		this.Hours-=12;	
}

Calendar.prototype.SetAmPm=SetAmPm;
// gets hour to show
function getShowHour() {
	var finalHour;

	if (TimeMode==12) {
		if (parseInt(this.Hours,10)==0) {
			this.AMorPM="AM";
			finalHour=parseInt(this.Hours,10)+12;	
		}

		else if (parseInt(this.Hours,10)==12) {
			this.AMorPM="PM";
			finalHour=12;
		}		

		else if (this.Hours>12)	{
			this.AMorPM="PM";
			if ((this.Hours-12)<10)

				finalHour="0"+((parseInt(this.Hours,10))-12);
			else
				finalHour=parseInt(this.Hours,10)-12;	
		}
		else {
			this.AMorPM="AM";
			if (this.Hours<10)

				finalHour="0"+parseInt(this.Hours,10);
			else
				finalHour=this.Hours;	
		}
	}

	else if (TimeMode==24) {
		if (this.Hours<10)
			finalHour="0"+parseInt(this.Hours,10);
		else	
			finalHour=this.Hours;
	}

	return finalHour;
}				

Calendar.prototype.getShowHour=getShowHour;		
//gets time in AM PM
function getShowAMorPM() {
	return this.AMorPM;	
}				

Calendar.prototype.getShowAMorPM=getShowAMorPM;		
//gets month name
function GetMonthName(IsLong) {
	var Month=MonthName[this.Month];
	if (IsLong)
		return Month;
	else
		return Month.substr(0,3);
}

Calendar.prototype.GetMonthName=GetMonthName;
//gets month days
function GetMonDays() { 

	var DaysInMonth=[31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
	if (this.IsLeapYear()) {
		DaysInMonth[1]=29;
	}	

	return DaysInMonth[this.Month];	
}

Calendar.prototype.GetMonDays=GetMonDays;
// checks if it a leap year
function IsLeapYear() {
	if ((this.Year%4)==0) {
		if ((this.Year%100==0) && (this.Year%400)!=0) {
			return false;
		}
		else {
			return true;
		}
	}
	else {
		return false;
	}

}

Calendar.prototype.IsLeapYear=IsLeapYear;
//formats date in required format
function FormatDate(pDate)
{
	var MonthDigit=this.Month+1;
	if (PrecedeZero==true) {
		if (pDate<10)
			pDate="0"+pDate;
		if (MonthDigit<10)
			MonthDigit="0"+MonthDigit;
	}

	if (this.Format.toUpperCase()=="DDMMYYYY")
		return (pDate+DateSeparator+MonthDigit+DateSeparator+this.Year);

	else if (this.Format.toUpperCase()=="DDMMMYYYY")
		return (pDate+DateSeparator+this.GetMonthName(false)+DateSeparator+this.Year);
	else if (this.Format.toUpperCase()=="MMDDYYYY")
		return (MonthDigit+DateSeparator+pDate+DateSeparator+this.Year);
	else if (this.Format.toUpperCase()=="MMMDDYYYY")
		return (this.GetMonthName(false)+DateSeparator+pDate+DateSeparator+this.Year);
	else if (this.Format.toUpperCase()=="YYYYMMDD")
		return (this.Year+DateSeparator+MonthDigit+DateSeparator+pDate);
	else if (this.Format.toUpperCase()=="YYMMDD")
		return (String(this.Year).substring(2,4)+DateSeparator+MonthDigit+DateSeparator+pDate);	
	else if (this.Format.toUpperCase()=="YYMMMDD")
		return (String(this.Year).substring(2,4)+DateSeparator+this.GetMonthName(false)+DateSeparator+pDate);				
	else if (this.Format.toUpperCase()=="YYYYMMMDD")
		return (this.Year+DateSeparator+this.GetMonthName(false)+DateSeparator+pDate);	
	else					
		return (pDate+DateSeparator+(this.Month+1)+DateSeparator+this.Year);
}
/**
 * This function updates date with new format
 */
function updateDateWithNewFormat(exDateTime,previousFormat,Format)
{
	if (exDateTime!="")	{

		var Sp1;
		var Sp2; 
		var tSp1;
		var tSp1;
		var strMonth;
		var strDate;
		var strYear;
		var intMonth;
		var YearPattern;

		Sp1=exDateTime.indexOf(DateSeparator,0);
		Sp2=exDateTime.indexOf(DateSeparator,(parseInt(Sp1)+1));

		var offset=parseInt(previousFormat.toUpperCase().lastIndexOf("M"))-parseInt(previousFormat.toUpperCase().indexOf("M"))-1;

		if ((previousFormat.toUpperCase()=="DDMMYYYY") || (previousFormat.toUpperCase()=="DDMMMYYYY")) {
			if (DateSeparator=="") {
				strMonth=exDateTime.substring(2,4+offset);
				strDate=exDateTime.substring(0,2);
				strYear=exDateTime.substring(4+offset,8+offset);
			}
			else {
				if(exDateTime.indexOf("D*") != -1) {   
					strMonth = exDateTime.substring(8, 11);
					strDate  = exDateTime.substring(0, 2);
					strYear  = "20" + exDateTime.substring(11, 13);  

				} else {
					strMonth=exDateTime.substring(Sp1+1,Sp2);
					strDate=exDateTime.substring(0,Sp1);
					strYear=exDateTime.substring(Sp2+1,Sp2+5);
				}
			}

		}

		else if ((previousFormat.toUpperCase()=="MMDDYYYY") || (previousFormat.toUpperCase()=="MMMDDYYYY")) {

			if (DateSeparator=="") {
				strMonth=exDateTime.substring(0,2+offset);
				strDate=exDateTime.substring(2+offset,4+offset);
				strYear=exDateTime.substring(4+offset,8+offset);
			}

			else {
				strMonth=exDateTime.substring(0,Sp1);
				strDate=exDateTime.substring(Sp1+1,Sp2);
				strYear=exDateTime.substring(Sp2+1,Sp2+5);
			}

		}

		else if ((previousFormat.toUpperCase()=="YYYYMMDD") || (previousFormat.toUpperCase()=="YYYYMMMDD")) {

			if (DateSeparator=="") {
				strMonth=exDateTime.substring(4,6+offset);
				strDate=exDateTime.substring(6+offset,8+offset);
				strYear=exDateTime.substring(0,4);
			}

			else {
				strMonth=exDateTime.substring(Sp1+1,Sp2);
				strDate=exDateTime.substring(Sp2+1,Sp2+3);
				strYear=exDateTime.substring(0,Sp1);
			}
		}
		else if ((previousFormat.toUpperCase()=="YYMMDD") || (previousFormat.toUpperCase()=="YYMMMDD")) {

			if (DateSeparator=="") {
				strMonth=exDateTime.substring(2,4+offset);
				strDate=exDateTime.substring(4+offset,6+offset);
				strYear=exDateTime.substring(0,2);
			}
			else {
				strMonth=exDateTime.substring(Sp1+1,Sp2);
				strDate=exDateTime.substring(Sp2+1,Sp2+3);
				strYear=exDateTime.substring(0,Sp1);
			}
		}		

	}

	this.Month = strMonth;
	this.Year = strYear;
	pDate = strDate;
	var MonthDigit=this.Month;

	if (Format.toUpperCase()=="DDMMYYYY")
		return (pDate+DateSeparator+MonthDigit+DateSeparator+this.Year);
	else if (Format.toUpperCase()=="DDMMMYYYY")
		return (pDate+DateSeparator+this.GetMonthName(false)+DateSeparator+this.Year);
	else if (Format.toUpperCase()=="MMDDYYYY")
		return (MonthDigit+DateSeparator+pDate+DateSeparator+this.Year);
	else if (Format.toUpperCase()=="MMMDDYYYY")
		return (this.GetMonthName(false)+DateSeparator+pDate+DateSeparator+this.Year);
	else if (Format.toUpperCase()=="YYYYMMDD")
		return (this.Year+DateSeparator+MonthDigit+DateSeparator+pDate);
	else if (Format.toUpperCase()=="YYMMDD")
		return (this.Year+DateSeparator+MonthDigit+DateSeparator+pDate);	
	else if (Format.toUpperCase()=="YYMMMDD")
		return (this.Year+DateSeparator+this.GetMonthName(false)+DateSeparator+pDate);				
	else if (Format.toUpperCase()=="YYYYMMMDD")
		return (this.Year+DateSeparator+this.GetMonthName(false)+DateSeparator+pDate);	
	else					
		return (pDate+DateSeparator+(this.Month+1)+DateSeparator+this.Year);
}

Calendar.prototype.FormatDate=FormatDate;

/**
 * This function closes popup
 */
function closewin(id) {
	var CalId = document.getElementById(id);

	CalId.focus();
	winCal.style.visibility='hidden';
}

/**
 * This function changes borders
 */
function changeBorder(element, col, oldBgColor) {
	if (col == 0) {
		element.style.background = HoverColor;
		element.style.borderColor = "black";
		element.style.cursor = "pointer";
	}

	else {
		if(oldBgColor) {
			element.style.background = oldBgColor;
		} else {
			element.style.background = "#E3F1F8"; 
			element.style.borderColor = ""
		}
		element.style.borderColor = "white";
		element.style.cursor = "auto";
	}
}

/**
 * This function makes calendar popup dragable
 */
function pickIt(evt) {
	if(calHeight == 0)
	{calHeight = 163;}

	
	if (window.addEventListener) { 

		var objectID = evt.target.id;
		if (objectID.indexOf(calSpanID) != -1){
			var dom = document.getElementById(objectID);
			cnLeft=evt.pageX;
			cnTop=evt.pageY;

			if (dom.offsetLeft){
				cnLeft = (cnLeft - dom.offsetLeft); cnTop = (cnTop - dom.offsetTop);
			}
		}

		
		var de = document.documentElement;
		var b = document.body;

		if((de.scrollLeft || b.scrollLeft) > 0){
			
			if((evt.pageX - (de.scrollLeft || b.scrollLeft)) > 322)
			{	
				xpos = (evt.pageX);
			}
			else{
				xpos = (evt.pageX) + 322;
			}			
		}
		else{xpos = (evt.pageX); }

		if((de.scrollTop || b.scrollTop) > 0){
		
			if((evt.pageY - (de.scrollTop || b.scrollTop)) > calHeight)
			{	
				ypos = (evt.pageY);
			}
			else{
				ypos = (evt.pageY) + calHeight;
			}
		}
		else{ypos = (evt.pageY); }
	}   

	else { 
		var objectID = event.srcElement.id;
		cnLeft=event.offsetX;
		cnTop=event.offsetY;

		
		var de = document.documentElement;
		var b = document.body;

		if((de.scrollLeft || b.scrollLeft) >0)
		{
			if((event.clientX - 322) > 0) 
			{	xpos = event.clientX + (de.scrollLeft || b.scrollLeft) - (de.clientLeft || 0);
			}
			else
			{	xpos = event.clientX + (de.scrollLeft || b.scrollLeft) +322;
			}
		}
		else
		{	
			xpos = event.clientX + (de.scrollLeft || b.scrollLeft) - (de.clientLeft || 0);

		}		

		if((de.scrollTop || b.scrollTop) >0) 
		{
			if((event.clientY - calHeight) > 0 ) 
			{	ypos = event.clientY + (de.scrollTop || b.scrollTop) - (de.clientTop || 0);
			}
			else
			{	ypos = event.clientY + (de.scrollTop || b.scrollTop) +calHeight;
			}
		}
		else
		{      

			ypos = event.clientY + (de.scrollTop || b.scrollTop) - (de.clientTop || 0);
		}
	}
  
	if ((objectID != undefined) && objectID.indexOf(calSpanID) != -1){ 
		domStyle = document.getElementById(objectID).style;
	}

	if (domStyle) { 
		domStyle.zIndex = 100; 
		return false;
	}

	else {
		domStyle = null; 
		return;
	}
}

/**
 * This function is called when popup is dragged
 */
function dragIt(evt) {
	if (domStyle) {
		if (window.event) { 
			domStyle.left = (event.clientX-cnLeft + document.body.scrollLeft)+'px';
			domStyle.top = (event.clientY-cnTop + document.body.scrollTop)+'px';
		} else {  
			domStyle.left = (evt.clientX-cnLeft + document.body.scrollLeft)+'px'; 
			domStyle.top = (evt.clientY-cnTop + document.body.scrollTop)+'px';
		}
	} 
}


/**
 * This function is called when popup is dropped
 */
function dropIt() {
	stopSpin();

	if (domStyle) { 
		domStyle = null;
	}
}


/**
 * This function starts spin
 */
function startSpin(whatSpinner, direction) {
	document.thisLoop = setInterval("nextStep('"+whatSpinner+"', '"+direction+"');", 125); 
}

/**
 * This function determines the next step
 */
function nextStep(whatSpinner, direction) {
	if(whatSpinner == "Hour") {
		if(direction == "plus") {
			Cal.SetHour(Cal.Hours + 1); RenderCssCal();
		} else if(direction == "minus") {
			Cal.SetHour(Cal.Hours - 1); RenderCssCal();
		}
	} else if(whatSpinner == "Minute") {
		if(direction == "plus") {
			Cal.SetMinute(parseInt(Cal.Minutes,10) + 1); RenderCssCal();
		} else if(direction == "minus") {
			Cal.SetMinute(parseInt(Cal.Minutes,10) - 1); RenderCssCal();
		}
	}

}

/**
 * This function stops spin
 */
function stopSpin() {
	clearInterval(document.thisLoop);
}

/**
 * This function detects browser
 */
function detectBrowser(){
	var bType;
	
	if(navigator.appName == "Netscape")
	{
		
		bType="moz";
	}
	if(navigator.appName == "Microsoft Internet Explorer")
	{
		
		bType="ie";
	}
	return bType;   
}

var Browser = {
		Version: function() {
	var version = 999; 
	if (navigator.appVersion.indexOf("MSIE") != -1)
		
		version = parseFloat(navigator.appVersion.split("MSIE")[1]);
	return version;
}
};
