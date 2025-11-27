package com.nyc.hhs.rule;

public class Input
{
	private String name;

	@Override
	public String toString()
	{
		return "Input [name=" + name + ", type=" + type + "]";
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	private String type;
}
