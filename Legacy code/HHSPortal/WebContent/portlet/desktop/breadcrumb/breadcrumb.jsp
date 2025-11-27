<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ page import="com.bea.netuix.servlets.controls.window.WindowPresentationContext,
                 com.bea.netuix.servlets.controls.window.TitlebarPresentationContext,
                 java.util.ArrayList,
                 java.util.Iterator,
                 com.bea.portlet.PageURL,
                 com.bea.netuix.servlets.controls.page.BookPresentationContext,
                 com.bea.netuix.servlets.controls.page.PagePresentationContext,
                 com.bea.netuix.servlets.controls.window.WindowCapabilities"
%>
<%@ page session="false"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib uri="http://www.bea.com/servers/portal/tags/netuix/render" prefix="render" %>

<%--
    This is the JSP for the showing breadcrumbs in XYZ Eagle portal.
    It is part of the skeleton file page.jsp.

    Implementation based on WebLogic Portal 10.2.

--%>

<%
    ArrayList breadcrumbTitles = new ArrayList();
    ArrayList breadcrumbURLs = new ArrayList();
    boolean isHidden = false;

    BookPresentationContext book = BookPresentationContext.getBookPresentationContext(request);
    PagePresentationContext pageCtx = PagePresentationContext.getPagePresentationContext(request);

    if (!(book.getDefaultPage().equals(pageCtx.getDefinitionLabel())))
    {
        breadcrumbTitles.add(book.getTitle());
        breadcrumbURLs.add(PageURL.createPageURL(request,response,book.getDefaultPage()).toString());
    }
   
	PagePresentationContext parentPage = book.getParentPagePresentationContext();
    while (parentPage != null)
    {
    	breadcrumbTitles.add(parentPage.getTitle());

        if (parentPage instanceof BookPresentationContext)
        {
        	BookPresentationContext parentBook = (BookPresentationContext)parentPage;
            breadcrumbURLs.add(PageURL.createPageURL(request, response,  parentBook.getDefaultPage()).toString());
        }
        else
        {
            breadcrumbURLs.add(PageURL.createPageURL(request, response, parentPage.getDefinitionLabel()).toString());
        }
   
        parentPage = parentPage.getParentPagePresentationContext();
    }
%>
         

<%            
	//DO NOT SHOW BREADCRUMBS IN MAIN HOME PAGE and also in hidden page
	if (pageCtx.isHidden())
	{
    }
    else
    {               
%>
<ul class="breadcrumbs">
<%
	for (int i = breadcrumbTitles.size() - 1; i >= 0; i--)
    {
    	if (((String)breadcrumbTitles.get(i)).equalsIgnoreCase("My Main Book")) {
        	breadcrumbTitles.set(i, "My Home");
        }
%>
<li class="first"><a href="<%=breadcrumbURLs.get(i) %>" ><%=breadcrumbTitles.get(i)%></a></li> <img src="<render:getSkinPath imageName="/arrow_right.gif" />" />&nbsp;  <%
	}
%>

<%
    }
%>
	</ul> 