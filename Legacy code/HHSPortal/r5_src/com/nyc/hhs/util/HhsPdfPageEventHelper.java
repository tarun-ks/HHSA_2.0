package com.nyc.hhs.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.nyc.hhs.constants.HHSConstants;

/**
 * This class is responsible to get Table Height align text and get content of
 * pdf file.
 * 
 */
public class HhsPdfPageEventHelper extends PdfPageEventHelper
{
	protected float msPdfTableHeight;
	
	public HhsPdfPageEventHelper()
	{
		super();
	}
	
	public float getTableHeight()
	{
		return msPdfTableHeight;
	}
	
	@Override
	public void onEndPage(PdfWriter writer, Document document)
	{
		PdfContentByte loPDFCb = writer.getDirectContent();
		ColumnText.showTextAligned(loPDFCb, Element.ALIGN_CENTER, new Phrase(HHSConstants.EMPTY_STRING),
				(document.left() + document.right()) / 2, document.bottom() - 20, 0);
	}
}
