package com.nyc.hhs.frameworks.grid;


/**
 * This class is a bean which maintains the state of the Link and is 
 * used by the LinkTag class to create a link element in the Grid.
 * 
 */

public class Link implements FormElement{
	
	private String msHref;
	private String msParamName;
	private String msParamValue;
	private String msOnClick;
	
	
	@Override
	public int getType() {
		return 3;
	}

	public Link(String asHref, String asParamName, String asParamValue, String asOnClick) {
		super();
		this.msHref = asHref;
		this.msParamName = asParamName;
		this.msParamValue = asParamValue;
		this.msOnClick=asOnClick;
	}

	public String getOnClick() {
		return msOnClick;
	}

	public void setOnClick(String asOnClick) {
		this.msOnClick = asOnClick;
	}

	public String getHref() {
		return msHref;
	}

	
	public void setHref(String asHref) {
		this.msHref = asHref;
	}

	public String getParamName() {
		return msParamName;
	}

	public void setParamName(String asParamName) {
		this.msParamName = asParamName;
	}

	public String getParamValue() {
		return msParamValue;
	}

	public void setParamValue(String asParamValue) {
		this.msParamValue = asParamValue;
	}

}
