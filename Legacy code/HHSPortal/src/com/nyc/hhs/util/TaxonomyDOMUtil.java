package com.nyc.hhs.util;

import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.springframework.util.CollectionUtils;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.TaxonomyTree;

/**
 * This class creates a DOM object of Taxonomy Data obtained from the database.
 */

public class TaxonomyDOMUtil
{

	/**
	 * This class creates a DOM object of Taxonomy Data obtained from the database.
	 * 
	 * @param aoTaxonomyList List of Taxonomy
	 * @return loDom  Document Object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "static-access", "rawtypes" })
	public Document createTaxonomyDOMObj(List<TaxonomyTree> aoTaxonomyList) throws ApplicationException
	{

		LogInfo loLogger = new LogInfo(TaxonomyDOMUtil.class);
		loLogger.Debug("Inside createTaxonomyDOMObj");
		Document loDom = null;
		try
		{
			Element loRootElement = new Element(ApplicationConstants.TAXONOMY_ELEMENT);
			loDom = new Document(loRootElement);
			if (!CollectionUtils.isEmpty(aoTaxonomyList))
			{
				Iterator loListItr = aoTaxonomyList.iterator();
				while (loListItr.hasNext())
				{
					TaxonomyTree loTaxonomy = (TaxonomyTree) loListItr.next();
					Element loChildDetails = new Element(ApplicationConstants.ELEMENT_NODE);
					loChildDetails.setAttribute("id", loTaxonomy.getMsElementid());
					loChildDetails.setAttribute("name", loTaxonomy.getMsElementName());
					loChildDetails.setAttribute("type", loTaxonomy.getMsElementType());
					loChildDetails.setAttribute("branchid", loTaxonomy.getMsBranchid());
					loChildDetails.setAttribute("parentid", loTaxonomy.getMsParentid());
					loChildDetails.setAttribute("evidencerequiredflag", loTaxonomy.getMsEvidenceReqd());
					loChildDetails.setAttribute("activeflag", loTaxonomy.getMsActiveFlag());
					loChildDetails.setAttribute("selectionflag", loTaxonomy.getMsActiveFlag());
					loChildDetails.setAttribute("level", loTaxonomy.getMsLevel());
					Element loChildDescription = new Element("description");
					loChildDescription.setText(loTaxonomy.getMsElementDescription());
					loChildDetails.addContent(loChildDescription);

					// Setting elements in DOM for level '1' Parent Taxonomy
					if (loTaxonomy.getMsLevel().toString().equalsIgnoreCase("0"))
					{
						loRootElement.addContent(loChildDetails);
					}
					// Setting elements in DOM for Child Taxonomy by searching
					// for their Parent in existing DOM
					else if (!loTaxonomy.getMsLevel().toString().equalsIgnoreCase("0"))
					{
						String lsXPath = "//element" + "[(@id='" + loTaxonomy.getMsParentid() + "')]";
						XMLUtil.getElement(lsXPath, loDom).addContent(loChildDetails);
					}
				}
			}
			loLogger.Debug(XMLUtil.getXMLAsString(loRootElement));
		}
		catch (ApplicationException loError)
		{
			loLogger.Error("Error occured while creating Taxonomy DOM Object", loError);
			throw new ApplicationException("Error occured while creating Taxonomy DOM Object", loError);
		}
		loLogger.Debug("createTaxonomyDOMObj Method Over");
		return loDom;

	}

}
