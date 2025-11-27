//This refreshes the financial summary
function onFinancialSummaryRefresh()
{
	$("#financialPortlet").loadingHome("Loading");
	hhsAjaxRender(null, document.getElementById("financialSummary"), "financialPortlet", document.getElementById("financialSummary").action, "loadingCallBack2");
}
//This function load call back
function loadingCallBack2()
{
	$("#financialPortlet").loadingHomeClose();
}