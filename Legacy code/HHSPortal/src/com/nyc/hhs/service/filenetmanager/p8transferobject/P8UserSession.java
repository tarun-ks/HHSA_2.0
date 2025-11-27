package com.nyc.hhs.service.filenetmanager.p8transferobject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.security.auth.Subject;

import org.apache.ibatis.session.SqlSession;

import com.filenet.api.collection.PageIterator;
import com.filenet.api.collection.PageMark;
import com.filenet.api.core.Connection;

import filenet.vw.api.VWSession;

/**
 * This is a DTO class which have multiple properties and getter/setter methods
 * and will store multiple details for the P8 session.
 * 
 */

public class P8UserSession implements Serializable
{

	private String userId;

	private String password;

	private String contentEngineUri;

	private String objectStoreName;

	private String isolatedRegionName;

	private Subject subject;

    private Connection connection;

	private PageIterator pageIterator;

	private List<PageMark> allPageMark;

	private int nextPageIndex;

	private int totalPageCount;

	private VWSession vwsession;

	private String objectsAllowedPerPage;

	private SqlSession filenetPEDBSession;

	private String isolatedRegionNumber;

	// added in R5
	private int currentlyFetchedPageCount;
	
	
	private PageIterator pageIteratorForTotal;
	

    @Override
    public String toString() {
        StringBuffer strb = new StringBuffer();
        if(subject == null ) {
            strb.append("Security Subject is NULL!!!!! \n");
        } else {
            Set<Object> allPrivatePrincipals = subject.getPrivateCredentials();

            strb.append("Number of Attributes "+allPrivatePrincipals.size() + "\n");
            strb.append("------------------------------------------------------------------------------------- \n");
            for ( Object principal : allPrivatePrincipals ) {
                strb.append(principal.toString()+ " \n");
            }
        }

        return "P8UserSession [userId=" + userId + ", password=" + password
                + ", contentEngineUri=" + contentEngineUri + ", objectStoreName=" + objectStoreName
                + ", isolatedRegionName=" + isolatedRegionName + ", connection=" + connection + ", \n subject="
                + strb.toString() + "]";
    }

	public PageIterator getPageIteratorForTotal()
	{
		return pageIteratorForTotal;
	}

	public void setPageIteratorForTotal(PageIterator pageIteratorForTotal)
	{
		this.pageIteratorForTotal = pageIteratorForTotal;
	}

	public int getCurrentlyFetchedPageCount()
	{
		return currentlyFetchedPageCount;
	}

	public void setCurrentlyFetchedPageCount(int currentlyFetchedPageCount)
	{
		this.currentlyFetchedPageCount = currentlyFetchedPageCount;
	}

	// r5 Changes ends
	/**
	 * @return the filenetPEDBSession
	 */
	public SqlSession getFilenetPEDBSession()
	{
		return filenetPEDBSession;
	}

	/**
	 * @param filenetPEDBSession the filenetPEDBSession to set
	 */
	public void setFilenetPEDBSession(SqlSession filenetPEDBSession)
	{
		this.filenetPEDBSession = filenetPEDBSession;
	}

	/**
	 * @return the userId
	 */
	public String getUserId()
	{
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	/**
	 * @return the password
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * @return the contentEngineUri
	 */
	public String getContentEngineUri()
	{
		return contentEngineUri;
	}

	/**
	 * @param contentEngineUri the contentEngineUri to set
	 */
	public void setContentEngineUri(String contentEngineUri)
	{
		this.contentEngineUri = contentEngineUri;
	}

	/**
	 * @return the objectStoreName
	 */
	public String getObjectStoreName()
	{
		return objectStoreName;
	}

	/**
	 * @param objectStoreName the objectStoreName to set
	 */
	public void setObjectStoreName(String objectStoreName)
	{
		this.objectStoreName = objectStoreName;
	}

	/**
	 * @return the isolatedRegionName
	 */
	public String getIsolatedRegionName()
	{
		return isolatedRegionName;
	}

	/**
	 * @param isolatedRegionName the isolatedRegionName to set
	 */
	public void setIsolatedRegionName(String isolatedRegionName)
	{
		this.isolatedRegionName = isolatedRegionName;
	}

	/**
	 * @return the subject
	 */
	public Subject getSubject()
	{
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(Subject subject)
	{
		this.subject = subject;
	}

	/**
	 * @return the connection
	 */
	public Connection getConnection()
	{
		return connection;
	}

	/**
	 * @param connection the connection to set
	 */
	public void setConnection(Connection connection)
	{
		this.connection = connection;
	}

	/**
	 * @return the pageIterator
	 */
	public PageIterator getPageIterator()
	{
		return pageIterator;
	}

	/**
	 * @param pageIterator the pageIterator to set
	 */
	public void setPageIterator(PageIterator pageIterator)
	{
		this.pageIterator = pageIterator;
	}

	/**
	 * @return the allPageMark
	 */
	public List<PageMark> getAllPageMark()
	{
		// R5 changes added
		return this.allPageMark;
		// R5 Changes ends
	}

	/**
	 * @param allPageMark the allPageMark to set
	 */
	public void setAllPageMark(List<PageMark> allPageMark)
	{
		// R5 changes added
		if (allPageMark != null)
			this.allPageMark = allPageMark;
		// R5 changes ends
	}

	/**
	 * @return the nextPageIndex
	 */
	public int getNextPageIndex()
	{
		return nextPageIndex;
	}

	/**
	 * @param nextPageIndex the nextPageIndex to set
	 */
	public void setNextPageIndex(int nextPageIndex)
	{
		this.nextPageIndex = nextPageIndex;
	}

	/**
	 * @return the totalPageCount
	 */
	public int getTotalPageCount()
	{
		return totalPageCount;
	}

	/**
	 * @param totalPageCount the totalPageCount to set
	 */
	public void setTotalPageCount(int totalPageCount)
	{
		this.totalPageCount = totalPageCount;
	}

	/**
	 * @return the vwsession
	 */
	public VWSession getVwsession()
	{
		return vwsession;
	}

	/**
	 * @param vwsession the vwsession to set
	 */
	public void setVwsession(VWSession vwsession)
	{
		this.vwsession = vwsession;
	}

	/**
	 * @return the objectsAllowedPerPage
	 */
	public String getObjectsAllowedPerPage()
	{
		return objectsAllowedPerPage;
	}

	/**
	 * @param objectsAllowedPerPage the objectsAllowedPerPage to set
	 */
	public void setObjectsAllowedPerPage(String objectsAllowedPerPage)
	{
		this.objectsAllowedPerPage = objectsAllowedPerPage;
	}

	/**
	 * @param getIsolatedRegionNumber the getIsolatedRegionNumber to set
	 */

	public String getIsolatedRegionNumber()
	{
		return isolatedRegionNumber;
	}

	/**
	 * @param setIsolatedRegionNumber the setIsolatedRegionNumber to set
	 */
	public void setIsolatedRegionNumber(String isolatedRegionNumber)
	{
		this.isolatedRegionNumber = isolatedRegionNumber;
	}

}