package com.nyc.hhs.frameworks.grid;

/**
 * This class is a bean which maintains the state of the ListOption and is 
 * used by the ListOptionTag class to create a ListOption element in the Grid.
 * 
 */

public class ListOption {

	public ListOption(String asValue) {
		this.msValue = asValue;
	}

	private String msValue;
	private String msSelected;

	
	public String getValue() {
		return msValue;
	}

	public void setValue(String asValue) {
		this.msValue = asValue;
	}

	public String getSelected() {
		if(null == msSelected){
			return "";
		}
		return msSelected;
		
	}

	public void setSelected(String asSelected) {
		this.msSelected = asSelected;
	}
}
