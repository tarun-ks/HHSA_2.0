package com.nyc.hhs.frameworks.sessiongrid;

/**
 * This class is a bean which maintains the state of the Column and is 
 * used by the ColumnTag class to create a Column element in the Grid.
 */

public class Column {
	
	private String msColumnName;
	private String msHeadingName;
	private boolean mbSort;
	private String msSortType;
	private String msSortValue;
	private String msAlign;
	private String msSize;
	private FormElement moElementType;
	
	public Column(String asColumnName, String asHeadingName) {
		super();

		this.msColumnName = asColumnName;
		this.msHeadingName = asHeadingName;
	}


	public String getAlign() {
		return msAlign;
	}

	public void setAlign(String asAlign) {
		this.msAlign = asAlign;
	}

	public String getSize() {
		return msSize;
	}

	public void setSize(String asSize) {
		this.msSize = asSize;
	}

	public String getColumnName() {
		return msColumnName;
	}

	public void setColumnName(String asColumnName) {
		this.msColumnName = asColumnName;
	}

	public String getHeadingName() {
		return msHeadingName;
	}

	public void setHeadingName(String asHeadingName) {
		this.msHeadingName = asHeadingName;
	}

	public FormElement getElementType() {
		return moElementType;
	}

	public void setElementType(FormElement aoElementType) {
		this.moElementType = aoElementType;
	}

	public boolean getSort()
	{
		return mbSort;
	}
	
	public void setSort(boolean abSort)
	{
		this.mbSort = abSort;
	}

	public String getSortType()
	{
		return msSortType;
	}

	public void setSortType(String asSortType) {
		this.msSortType = asSortType;
	}

	public String getSortValue() {
		return msSortValue;
	}

	public void setSortValue(String asSortValue) {
		this.msSortValue = asSortValue;
	}
	
	

}
