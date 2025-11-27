package com.nyc.hhs.frameworks.grid;

/**
 * This class is a bean which maintains the state of the custom extension and is 
 * used by the ExtensionTag class to create a custom extension element in the Grid.
 * 
 */

public class Extension implements FormElement
{
	private String msDecoratorClass;
	
	public Extension(String asDecoratorClass)
	{
		this.msDecoratorClass = asDecoratorClass;
	}

	public String getDecoratorClass()
	{
		return msDecoratorClass;
	}

	public void setDecoratorClass(String decoratorClass)
	{
		this.msDecoratorClass = decoratorClass;
	}

	
	public int getType()
	{
		return 6;
	}
}
