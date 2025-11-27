package com.nyc.hhs.rule;

public class Output
{
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public String getType()
	{
		return type;
	}

	@Override
	public String toString()
	{
		return "Output [name=" + name + ", value=" + value + ", type=" + type + ", method=" + method + "]";
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getMethod()
	{
		return method;
	}

	public void setMethod(String method)
	{
		this.method = method;
	}

	private String name;
	private String value;
	private String type;
	private String method;
}
