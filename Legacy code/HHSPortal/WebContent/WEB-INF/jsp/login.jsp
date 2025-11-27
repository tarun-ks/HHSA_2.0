<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects />
<h3 align="center">HHS Portal Login/Logout</h3>
<%
   if (request.getUserPrincipal() == null)
   {
      String errorMessage = (String) request.getAttribute("loginErrorMessage3");
%>
<form method="post" id="backing" action="<portlet:actionURL/>" type="POST">
   <table border="0" width="100%">
      <tr>
           <td align="center" colspan="2">
              <% if (errorMessage != null) { %>
              <font color="red">Login failed. Please try again.</font><br>
              <% } %>
              Please enter your username and password below.<br>
           </td>
      </tr>
      <tr>
           <td align="right">
              UserName:
           </td>
           <td align="left">
              <input type="text" size=15 name="username" >
           </td>
      </tr>
      <tr>
           <td align="right">
              Password:
           </td>
           <td align="left">
              <input type="password" size=15 name="password" >
           </td>
      </tr>
      <tr>
           <td colspan="2" align="center">
              <input type="submit" title="Login" value="Login">
           </td>
      </tr>
   </table>
</form>
<%
   }
%>
