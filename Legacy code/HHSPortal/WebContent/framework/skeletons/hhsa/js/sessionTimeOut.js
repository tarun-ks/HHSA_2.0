var idleTime = ''; // number of miliseconds until the user is considered idle
var modelPopup = '<div id="sessionTimeoutWarning" style="display: none"></div>';
var initialSessionTimeoutMessage = '<div class="sessionTimeOutLogo">Your HHS Accelerator session will end in   '+ 
	'<span id="sessionTimeoutCountdown"></span>&nbsp;<br/><br/>Click <b>Continue</b> '+
	'if you would like to continue using HHS Accelerator. <br/> Click <b>Exit</b> if you would like to exit HHS Accelerator.</div>';
var sessionTimeoutCountdownId = 'sessionTimeoutCountdown';
var redirectAfter = 10; // number of seconds to wait before redirecting the user
var redirectTo = '';//'?_nfpb=true&_pageLabel=portlet_hhsweb_portal_login_page&_nfls=false&logout=logout';
var keepAliveURL = '/js_sandbox/'; // URL to call to keep the session alive
var expiredMessage = 'Your session has expired.  You are being logged out for security reasons.'; // message to show user when the countdown reaches 0
var running = false; // var to check if the countdown is running
var timer; // reference to the setInterval timer so it can be stopped
$(document).ready(function() {
	if($("#sessionTimeOutLogin").val() && $("#sessionTimeOutLogin").val()!='false'){
		if($("#typeOfUser").attr("value")=='provider_org'){
			redirectTo = $("#contextPathSession").attr("value")+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_login_page&_nfls=false&logout=logout&app_menu_name=logout_icon" ;
		}else{
		  // start QC 9205 R 8.0.0 Internal SAML 
		  //redirectTo = $("#contextPathSession").attr("value")+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_login_page&_nfls=false&logout=logout&siteminderLogout=siteminderLogout&app_menu_name=logout_icon";
			redirectTo = $("#contextPathSession").attr("value")+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_login_page&_nfls=false&logout=logout&app_menu_name=logout_icon" ;
		 // end QC 9205 R 8.0.0 Internal SAML 
		}
		idleTime = ($("#sessionTimeOut").val()-parseInt(2))*60*1000;// session time out defined in hhs properties 
		redirectAfter = 120; // 2 mins in seconds;
	// create the warning window and set autoOpen to false
	var sessionTimeoutWarningDialog = $(modelPopup);
	$(sessionTimeoutWarningDialog).html(initialSessionTimeoutMessage);
	$(sessionTimeoutWarningDialog).dialog({
		title: 'HHS Accelerator TimeOut',
		autoOpen: false,	// set this to false so we can manually open it
		closeOnEscape: false,
		draggable: false,
		width: 626,
		minHeight: 70, 
		modal: true,
		dialogClass: 'dialogButtons',
		beforeclose: function() { // bind to beforeclose so if the user clicks on the "X" or escape to close the dialog, it will work too
			// stop the timer
			clearInterval(timer);
			// stop countdown
			running = false;
			// ajax call to keep the server-side session alive
			$.ajax({
			  url: keepAliveURL,
			  async: false
			});
		},
		buttons: {
			Continue: function() {
				// close dialog
				$(this).dialog('close');
			},
			Exit: function() {
				// close dialog
				location.href= redirectTo;
			}
		},
		resizable: false,
		open: function() {
			// scrollbar fix for IE
			$('body').css('overflow','hidden');
		},
		close: function() {
			// reset overflow
			$('body').css('overflow','auto');
		}
	}); // end of dialog
	$("div.dialogButtons div button:nth-child(1)").attr("title","Continue");
	$("div.dialogButtons div button:nth-child(2)").attr("title","Exit");
	// start the idle timer
	$.idleTimer(idleTime);
	// bind to idleTimer's idle.idleTimer event
	$(document).bind("idle.idleTimer", function(){
		// if the user is idle and a countdown isn't already running
		if($.data(document,'idleTimer') === 'idle' && !running){
			var counter = redirectAfter;
			running = true;
			// intialisze timer
			var timeToDisplay = redirectAfter;
			timeToDisplay = (timeToDisplay/60)+":00";
			$('#'+sessionTimeoutCountdownId).html(timeToDisplay);
			// open dialog
			$(sessionTimeoutWarningDialog).dialog('open');
			// create a timer that runs every second
			timer = setInterval(function(){
				counter -= 1;
				// if the counter is 0, redirect the user
				if(counter === 0) {
					$(sessionTimeoutWarningDialog).html(expiredMessage);
					$(sessionTimeoutWarningDialog).dialog('disable');
					location.href= redirectTo;
				} else {
					timeToDisplay = counter;
					minutes = parseInt( timeToDisplay / 60 ) % 60;
					seconds = timeToDisplay % 60;
					$('#'+sessionTimeoutCountdownId).html(minutes+":"+(seconds<=9?"0"+seconds:seconds));
				};
			}, 1000);
		};
	});
	}
});

(function($){
$.idleTimer = function f(newTimeout){
    var idle    = false,        //indicates if the user is idle
        enabled = true,        //indicates if the idle timer is enabled
        timeout = 30000,        //the amount of time (ms) before the user is considered idle
        events  = 'mousemove keydown DOMMouseScroll mousewheel mousedown', // activity is one of these events
    /* (intentionally not documented)
     * Toggles the idle state and fires an appropriate event.
     * @return {void}
     */
    toggleIdleState = function(){
        //toggle the state
        idle = !idle;
        // reset timeout counter
        f.olddate = +new Date;
        //fire appropriate event
        $(document).trigger(  $.data(document,'idleTimer', idle ? "idle" : "active" )  + '.idleTimer');            
    },
    /**
     * Stops the idle timer. This removes appropriate event handlers
     * and cancels any pending timeouts.
     * @return {void}
     * @method stop
     * @static
     */         
    stop = function(){
        //set to disabled
        enabled = false;
        //clear any pending timeouts
        clearTimeout($.idleTimer.tId);
        //detach the event handlers
        $(document).unbind('.idleTimer');
    },
    /* (intentionally not documented)
     * Handles a user event indicating that the user isn't idle.
     * @param {Event} event A DOM2-normalized event object.
     * @return {void}
     */
    handleUserEvent = function(){
        //clear any existing timeout
        clearTimeout($.idleTimer.tId);
        //if the idle timer is enabled
        if (enabled){
            //if it's idle, that means the user is no longer idle
            if (idle){
                toggleIdleState();           
            } 
            //set a new timeout
            $.idleTimer.tId = setTimeout(toggleIdleState, timeout);
        }    
     };
    /**
     * Starts the idle timer. This adds appropriate event handlers
     * and starts the first timeout.
     * @param {int} newTimeout (Optional) A new value for the timeout period in ms.
     * @return {void}
     * @method $.idleTimer
     * @static
     */ 
    f.olddate = f.olddate || +new Date;
    //assign a new timeout if necessary
    if (typeof newTimeout == "number"){
        timeout = newTimeout;
    } else if (newTimeout === 'destroy') {
        stop();
        return this;  
    } else if (newTimeout === 'getElapsedTime'){
        return (+new Date) - f.olddate;
    }
    //assign appropriate event handlers
    $(document).bind($.trim((events+' ').split(' ').join('.idleTimer ')),handleUserEvent);
    //set a timeout to toggle state
    $.idleTimer.tId = setTimeout(toggleIdleState, timeout);
    // assume the user is active for the first x seconds.
    $.data(document,'idleTimer',"active");
}; // end of $.idleTimer()
})(jQuery);