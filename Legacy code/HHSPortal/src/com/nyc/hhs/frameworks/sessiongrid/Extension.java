package com.nyc.hhs.frameworks.sessiongrid;

/**
 * This class is a bean which maintains the state of the custom extension and is 
 * used by the ExtensionTag class to create a custom extension element in the Grid.
 * 
 */

public class Extension implements FormElement {

	public Extension(String decoratorClass) {
		this.decoratorClass = decoratorClass;
	}

	private String decoratorClass;

	public String getDecoratorClass() {
		return decoratorClass;
	}

	public void setDecoratorClass(String decoratorClass) {
		this.decoratorClass = decoratorClass;
	}

	@Override
	public int getType() {
		return 6;
	}
}
