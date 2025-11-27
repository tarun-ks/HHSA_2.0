package com.nyc.hhs.frameworks.grid;

/**
 * This class is a bean which maintains the state of the Text-Field and is 
 * used by the TextFieldTag class to create a Text-Field element in the Grid.
 * 
 */

public class TextField implements FormElement {

	public TextField(String name) {
		this.msName = name;
	}

	@Override
	public int getType() {
		return 4;
	}

	private String msName;
	private String msDefaultValue;
	private int miSize;
	private int miMaxlength;

	public String getName() {
		return msName;
	}

	public void setName(String asName) {
		this.msName = asName;
	}

	public String getDefaultValue() {
		return msDefaultValue;
	}

	public void setDefaultValue(String asDefaultValue) {
		this.msDefaultValue = asDefaultValue;
	}

	public int getSize() {
		return miSize;
	}

	public void setSize(int aiSize) {
		this.miSize = aiSize;
	}

	public int getMaxlength() {
		return miMaxlength;
	}

	public void setMaxlength(int aiMaxlength) {
		this.miMaxlength = aiMaxlength;
	}
}
