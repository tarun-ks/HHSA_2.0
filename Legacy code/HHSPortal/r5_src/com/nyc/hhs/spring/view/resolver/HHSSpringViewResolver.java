package com.nyc.hhs.spring.view.resolver;

import java.io.InputStream;

import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
/**
 *This is a utility class to get alternate prefix
 */
public class HHSSpringViewResolver extends InternalResourceViewResolver
{
	String alternatePrefix;

	public String getAlternatePrefix()
	{
		return alternatePrefix;
	}

	public void setAlternatePrefix(String alternatePrefix)
	{
		this.alternatePrefix = alternatePrefix;
	}

	public HHSSpringViewResolver()
	{
		setViewClass(InternalResourceView.class);
	}
/**
 * This method is used to get build view
 */
	@Override
	protected AbstractUrlBasedView buildView(String viewName) throws Exception
	{
		String url = getPrefix() + viewName + getSuffix();
		InputStream stream = getServletContext().getResourceAsStream(url);
		AbstractUrlBasedView loAbstractUrlBasedView = null;
		if (stream == null)
		{
			String lsPrefix = getPrefix();
			setPrefix(alternatePrefix);
			loAbstractUrlBasedView = super.buildView(viewName);
			setPrefix(lsPrefix);
		}
		else
		{
			loAbstractUrlBasedView = super.buildView(viewName);
		}
		return loAbstractUrlBasedView;
	}
}