package com.nyc.hhs.frameworks.sessiongrid;

/**
 * This class is a bean which maintains the state of the Check box and is 
 * used by the CheckBoxTag class to create a Check box element in the Grid.
 *
 */
public class CheckBox implements FormElement{

	public CheckBox(String name,String value){
		this.name = name;
		this.value = value;
	}
	
	@Override
	public int getType() {
		return 2;
	}
	
	private String name;
	private String value;

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

}
