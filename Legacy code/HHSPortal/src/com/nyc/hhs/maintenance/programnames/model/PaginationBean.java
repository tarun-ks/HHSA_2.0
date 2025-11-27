package com.nyc.hhs.maintenance.programnames.model;

public class PaginationBean {
	private Long totalDataCount = 0L;
	
	private Long rowsInPage = 20L;
	private Long totalPageCount = 0L;
	
	private Long currentPage = 0L;
	private Long startPage = 0L;
	private Long endPage = 0L;
	
	private String searchWord = "";
	private String searchAgencyId = "";
    private String searchStatus = "";
    private String searchModifiedFrom = "";
    private String searchModifiedTo = "";
    private String searchCreatedFrom = "";
    private String searchCreatedTo = "";
    private String searchSortOrder = "";
	
	public PaginationBean( long curPage, long rowsInPage, String searchWord, String searchSortOrder){

		this.currentPage = curPage;
		if( rowsInPage > 0L ){
		    this.rowsInPage  = rowsInPage;
		}
		this.searchWord  = searchWord;
		this.searchSortOrder = searchSortOrder;

	}

	public PaginationBean( long curPage, long rowsInPage){
		this.currentPage = curPage;
        if( rowsInPage > 0L ){
            this.rowsInPage  = rowsInPage;
        }
	}
	
	public PaginationBean( ){
		super();
	}

	public Long getTotalDataCount() {
		return totalDataCount;
	}
	public void setTotalDataCount(Long totalDataCount) {
		this.totalDataCount = totalDataCount;
	}
	public Long getRowsInPage() {
		return rowsInPage;
	}
	public void setRowsInPage(Long rowsInPage) {
        if( rowsInPage > 0L ){
            this.rowsInPage  = rowsInPage;
        }
	}
	public Long getTotalPageCount() {
		return totalPageCount;
	}
	public void setTotalPageCount(Long totalPageCount) {
		this.totalPageCount = totalPageCount;
	}
	public Long getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(Long currentPage) {
		this.currentPage = currentPage;
	}
	public Long getStartPage() {
		return startPage;
	}
	public void setStartPage(Long startPage) {
		this.startPage = startPage;
	}
	public Long getEndPage() {
		return endPage;
	}
	public void setEndPage(Long endPage) {
		this.endPage = endPage;
	}
	
	public String getSearchWord() {
		return searchWord;
	}

	public void setSearchWord(String searchWord) {
		if(searchWord != null )
			this.searchWord = searchWord.toLowerCase();
		else 
			this.searchWord = "";
	}

	public String getSearchAgencyId() {
        return searchAgencyId;
    }

    public void setSearchAgencyId(String searchAgencyId) {
        this.searchAgencyId = searchAgencyId;
    }

    public String getSearchStatus() {
        return searchStatus;
    }

    public void setSearchStatus(String searchStatus) {
        this.searchStatus = searchStatus;
    }

    public String getSearchModifiedFrom() {
        return searchModifiedFrom;
    }

    public void setSearchModifiedFrom(String searchModifiedFrom) {
        this.searchModifiedFrom = searchModifiedFrom;
    }

    public String getSearchModifiedTo() {
        return searchModifiedTo;
    }

    public void setSearchModifiedTo(String searchModifiedTo) {
        this.searchModifiedTo = searchModifiedTo;
    }

    public String getSearchCreatedFrom() {
        return searchCreatedFrom;
    }

    public void setSearchCreatedFrom(String searchCreatedFrom) {
        this.searchCreatedFrom = searchCreatedFrom;
    }

    public String getSearchCreatedTo() {
        return searchCreatedTo;
    }

    public void setSearchCreatedTo(String searchCreatedTo) {
        this.searchCreatedTo = searchCreatedTo;
    }

    public String getSearchSortOrder() {
        return searchSortOrder;
    }

    public void setSearchSortOrder(String searchSortOrder) {
        this.searchSortOrder = searchSortOrder;
    }

    public void escape(){
        if(this.searchWord != null)
            this.searchWord = this.searchWord.replaceAll("'", "''");
    }
    
    public void unEscape(){
        if(this.searchWord != null)
            this.searchWord = this.searchWord.replaceAll("''", "'");
    }
    
    public void copySearchParam(PaginationBean pageInfo){
	    pageInfo.setSearchWord( this.searchWord) ;
	    pageInfo.setSearchAgencyId( this.searchAgencyId) ;
	    pageInfo.setSearchStatus( this.searchStatus) ;
	    pageInfo.setSearchModifiedFrom( this.searchModifiedFrom) ;
	    pageInfo.setSearchModifiedTo( this.searchModifiedTo) ;
	    pageInfo.setSearchCreatedFrom( this.searchCreatedFrom) ;
	    pageInfo.setSearchCreatedTo( this.searchCreatedTo) ;
	    pageInfo.setSearchSortOrder( this.searchSortOrder) ;
	}
}


