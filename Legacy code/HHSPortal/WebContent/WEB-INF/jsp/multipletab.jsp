<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%-- [Start] 9.4.0 qc 9656 Invoice Review task may create same payment more than once due to multi-tab --%>

<c:if test="${ (multipletab_taskName != null) and (not empty multipletab_taskName) and (org_type eq 'agency_org')}">
	<script type="text/javascript">
	(function ($) {
	    $.fn.DuplicateWindow = function () {
	        var localStorageTimeout = (15) * 1000; // 15,000 milliseconds = 15 seconds.
	        var localStorageResetInterval = (5) * 1000; // 5,000 milliseconds = 5 seconds.
	        var localStorageTabKey = 'hhsportal-application-${multipletab_taskName}-browser-tab';
	        
	        var ItemType = {
	            Session: 1,
	            Local: 2
	        };
	
	        function setCookie(name, value, days) {
	            var expires = "";
	            if (days) {
	                var date = new Date();
	                date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
	                expires = "; expires=" + date.toUTCString();
	            }
	            document.cookie = name + "=" + (value || "") + expires + "; path=/";
	        }
	        function getCookie(name) {
	            var nameEQ = name + "=";
	            var ca = document.cookie.split(';');
	            for (var i = 0; i < ca.length; i++) {
	                var c = ca[i];
	                while (c.charAt(0) == ' ') c = c.substring(1, c.length);
	                if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
	            }
	            return null;
	        }
	
	        function GetItem(itemtype) {
	            var val = "";
	            switch (itemtype) {
	                case ItemType.Session:
	                    val = window.name;
	                    break;
	                case ItemType.Local:
	                    val = decodeURIComponent(getCookie(localStorageTabKey));
	                    if (val == undefined)
	                        val = "";
	                    break;
	            }
	            return val;
	        }
	
	        function SetItem(itemtype, val) {
	            switch (itemtype) {
	                case ItemType.Session:
	                    window.name = val;
	                    break;
	                case ItemType.Local:
	                    setCookie(localStorageTabKey, val);
	                    break;
	            }
	        }
	
	        function createGUID() {
	            this.s4 = function () {
	                return Math.floor((1 + Math.random()) * 0x10000)
	                  .toString(16)
	                  .substring(1);
	            };
	            return this.s4() + this.s4() + '-' + this.s4() + '-' + this.s4() + '-' + this.s4() + '-' + this.s4() + this.s4() + this.s4();
	        }
	      function TestIfDuplicate() {
	            //console.log("In testTab");
	            var sessionGuid = GetItem(ItemType.Session) || createGUID();
	            SetItem(ItemType.Session, sessionGuid);
	
	            var val = GetItem(ItemType.Local);
	            var tabObj = (val == "" ? null : JSON.parse(val)) || null;
	            console.log(val);
	            console.log(sessionGuid);
	            console.log(tabObj);
	
	            // If no or stale tab object, our session is the winner.  If the guid matches, ours is still the winner
	            if (tabObj === null || (tabObj.timestamp < (new Date().getTime() - localStorageTimeout)) || tabObj.guid === sessionGuid) {
	                function setTabObj() {
	                    //console.log("In setTabObj");
	                    var newTabObj = {
	                        guid: sessionGuid,
	                        timestamp: new Date().getTime()
	                    };
	                    SetItem(ItemType.Local, JSON.stringify(newTabObj));
	                }
	                setTabObj();
	                setInterval(setTabObj, localStorageResetInterval);//every x interval refresh timestamp in cookie
	                return false;
	            } else {
	                // An active tab is already open that does not match our session guid.
	                return true;
	            }
	        }
	
	        window.IsDuplicate = function () {
	            var duplicate = TestIfDuplicate();
	            //console.log("Is Duplicate: "+ duplicate);
	            return duplicate;
	        };
	
	        $(window).on("beforeunload", function () {
	            if (TestIfDuplicate() == false) {
	                SetItem(ItemType.Local, "");
	            }
	        })
	    }
	    $(window).DuplicateWindow();
	}(jQuery));
	
	  $(document).ready(function () {
          if (window.IsDuplicate()) {        
			 window.location.href = "${pageContext.servletContext.contextPath}/error/multipletabmessage.jsp";
          }
					
      });		
	</script>
	
</c:if>

<%-- [End] 9.4.0 qc 9656 Invoice Review task may create same payment more than once due to multi-tab --%>