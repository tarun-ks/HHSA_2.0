package com.nyc.hhs.frameworks.grid;

/**
 *  This class is a bean which maintains the state of the Radio Button and is 
 *  used by the RadioTag class to create a Radio Button element in the Grid.
 * 
 */

public class RadioButton implements FormElement{

	public RadioButton(String asGroupName, String asValue) {
		this.msGroupName = asGroupName;
		this.msValue = asValue;
	}

	private String msGroupName;
	private String msValue;

	
	public String getGroupName() {
		return msGroupName;
	}

	public void setGroupName(String asGroupName) {
		this.msGroupName = asGroupName;
	}

	public String getValue() {
		return msValue;
	}

	public void setValue(String asValue) {
		this.msValue = asValue;
	}

	@Override
	public int getType() {
		return 1;
	}
}
