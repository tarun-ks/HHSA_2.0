package com.nyc.hhs.util;

import java.util.Comparator;

import com.nyc.hhs.daomanager.service.ApplicationSummaryService.SortParameter;
import com.nyc.hhs.model.ApplicationSummary;

/**
 * This is custom comparator which is going to sort the list based on the
 * multiple property
 */
public class CustomComparator implements Comparator<ApplicationSummary>
{

	private SortParameter[] moParameters;

	/**
	 * parameter constructor
	 * 
	 * @param aoParameters
	 */
	// added for release 5
	private CustomComparator(SortParameter[] aoParameters)
	{
		this.moParameters = aoParameters.clone();
	}

	// added for release 5

	/**
	 * This is used to call the comparator
	 * 
	 * @param aoSortParameters sorting parameters
	 * @return comparator
	 */
	public static Comparator<ApplicationSummary> getComparator(SortParameter... aoSortParameters)
	{
		return new CustomComparator(aoSortParameters);
	}

	/**
	 * This is override method of the comparator that compare two object
	 * property
	 * 
	 * @param ApplicationSummary application summary object 1
	 * @param ApplicationSummary application summary object 2
	 */
	public int compare(ApplicationSummary aoAppSummary1, ApplicationSummary aoAppSummary2)
	{
		int liComparison;
		for (SortParameter loParameter : moParameters)
		{
			switch (loParameter)
			{
				case DATE:
					if (aoAppSummary1.getMdAppSubmissionDate() == null)
					{
						liComparison = (aoAppSummary2.getMdAppSubmissionDate() == null) ? 0 : -1;
					}
					else if (aoAppSummary2.getMdAppSubmissionDate() == null)
					{
						liComparison = 1;
					}
					else
					{
						liComparison = aoAppSummary1.getMdAppSubmissionDate().compareTo(
								aoAppSummary2.getMdAppSubmissionDate());
					}
					if (liComparison != 0)
					{
						return liComparison;
					}
					break;
				case NAME_ASCENDING:
					liComparison = (aoAppSummary1.getMsAppName()).compareTo(aoAppSummary2.getMsAppName());
					if (liComparison != 0)
					{
						return liComparison;
					}
					break;
			}
		}
		return 0;
	}
}
