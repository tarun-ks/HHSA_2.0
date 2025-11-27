package com.nyc.hhs.frameworks.sessiongrid;

/**
 * This class is a bean which maintains the state of the ListOption and is 
 * used by the ListOptionTag class to create a ListOption element in the Grid.
 * 
 */

public class ListOption {

	public ListOption(String value) {
		this.value = value;
	}

	private String value;
	private String selected;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getSelected() {
		if(null == selected){
			return "";
		}
		return selected;
		
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}
}
