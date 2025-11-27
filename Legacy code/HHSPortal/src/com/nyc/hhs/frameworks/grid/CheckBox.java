/**
 * 
 */
package com.nyc.hhs.frameworks.grid;

/**
 * This class is a bean which maintains the state of the Check box and is 
 * used by the CheckBoxTag class to create a Checkbox element in the Grid.
 * 
 */

public class CheckBox implements FormElement
{

	private String msName;
	private String msValue;
	
	public CheckBox(String asName, String asValue)
	{
		this.msName = asName;
		this.msValue = asValue;
	}

	@Override
	public int getType()
	{
		return 2;
	}

	public String getName()
	{
		return msName;
	}

	public void setName(String asName)
	{
		this.msName = asName;
	}

	public String getValue()
	{
		return msValue;
	}

	public void setValue(String asValue)
	{
		this.msValue = asValue;
	}

}
