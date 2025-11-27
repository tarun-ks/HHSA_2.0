package com.nyc.hhs.frameworks.sessiongrid;

/**
 * This class is a bean which maintains the state of the Text-Field and is 
 * used by the TextFieldTag class to create a Text-Field element in the Grid.
 * 
 */

public class TextField implements FormElement {

	public TextField(String name) {
		this.name = name;
	}

	@Override
	public int getType() {
		return 4;
	}

	private String name;
	private String defaultValue;
	private int size;
	private int maxlength;

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getMaxlength() {
		return maxlength;
	}

	public void setMaxlength(int maxlength) {
		this.maxlength = maxlength;
	}
}
