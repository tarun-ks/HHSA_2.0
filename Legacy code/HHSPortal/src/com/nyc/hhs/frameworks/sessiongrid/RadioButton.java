package com.nyc.hhs.frameworks.sessiongrid;

/**
 * This class is a bean which maintains the state of the Radio Button and is 
 * used by the RadioTag class to create a Radio Button element in the Grid.
 * 
 */

public class RadioButton implements FormElement{

	public RadioButton(String groupName, String value) {
		this.groupName = groupName;
		this.value = value;
	}

	private String groupName;
	private String value;

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getType() {
		return 1;
	}
}
