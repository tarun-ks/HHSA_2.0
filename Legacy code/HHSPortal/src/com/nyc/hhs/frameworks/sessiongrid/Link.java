package com.nyc.hhs.frameworks.sessiongrid;


/**
 * This class is a bean which maintains the state of the Link and is 
 * used by the LinkTag class to create a link element in the Grid.
 * 
 */

public class Link implements FormElement{
	
	
	
	@Override
	public int getType() {
		return 3;
	}

	public Link(String href, String paramName, String paramValue, String onClick) {
		super();
		this.href = href;
		this.paramName = paramName;
		this.paramValue = paramValue;
		this.onClick=onClick;
	}

	public String getOnClick() {
		return onClick;
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}

	private String href;
	private String paramName;
	private String paramValue;
	private String onClick;

	public String getHref() {
		return href;
	}

	
	public void setHref(String href) {
		this.href = href;
	}

	public String getParamName() {
		return paramName;
	}

	
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	
	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

}
