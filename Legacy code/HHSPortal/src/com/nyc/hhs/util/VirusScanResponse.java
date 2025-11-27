package com.nyc.hhs.util;

/**
 * This utility class scans viruses while uploading documents via document vault
 * or application and checks whether or not a document is infected.
 * 
 */

public class VirusScanResponse
{

	/**
	 * This method will continue to check the response further
	 * 
	 * @param server_response
	 *            a string value of server response object
	 * @return a boolean value indicating return value
	 */
	public boolean continue_check(String asServerResponse)
	{
		boolean lbRetValue = true;
		if (asServerResponse.length() != 12)
		{
			asServerResponse = asServerResponse.substring(0, 12);
		}
		if (asServerResponse.startsWith("ICAP/1.0 100"))
		{
			lbRetValue = true;
		}
		else
		{
			lbRetValue = false;
		}
		return lbRetValue;
	}

	/**
	 * This method will return virus name if response contains vrus
	 * 
	 * @param asServerResponse
	 *            a string value of server response
	 * @return a string value of request
	 */
	public String request(String asServerResponse)
	{
		String lsReturnString, lsBlockResult = "", lsStatus = "Undefined";
		StringBuffer lsBvirusName = new StringBuffer();
		String[] loLines = asServerResponse.split("\n");
		for (int liCount = 0; liCount < loLines.length; liCount++)
		{
			if (loLines[liCount].startsWith("ICAP/1.0 204"))
			{
				lsStatus = "Clean:";
				lsBlockResult = "0";
				lsBvirusName.append("No malware detected;");
				break;
			}
			if (loLines[liCount].startsWith("ICAP/1.0 200"))
			{
				lsStatus = "Blocked:";
				lsBlockResult = "200";
				continue;
			}
			if (loLines[liCount].startsWith("X-Virus-Name:"))
			{
				lsStatus = "Infected:";
				if ((loLines[liCount].substring(14)).startsWith(lsBvirusName.substring(0, lsBvirusName.length() - 1)))
				{
					lsBvirusName = new StringBuffer(loLines[liCount].substring(14));
				}
				else
				{
					lsBvirusName.append(loLines[liCount].substring(14));
				}
				lsBvirusName.append(";");
				continue;
			}
			if (loLines[liCount].startsWith("X-WWBlockResult:"))
			{
				lsBlockResult = loLines[liCount].substring(17);
				switch (Integer.parseInt(lsBlockResult))
				{
					case 90:
						lsBvirusName.append("Policy:Unwanted Unsigned Content;");
						break;
					case 81:
						lsBvirusName.append("Policy:Authorization needed;");
						break;
					case 45:
						lsBvirusName.append("Policy:Macros unwanted;");
						break;
					case 44:
						lsBvirusName.append("Policy:Office document unreadable;");
						break;
					case 43:
						lsBvirusName.append("Policy:Encrypted document unwanted;");
						break;
					case 42:
						lsBvirusName.append("Policy:ActiveX unwanted;");
						break;
					case 41:
						lsBvirusName.append("Policy:Document Inspector;");
						break;
					case 40:
						lsBvirusName.append("Policy:Text Categorization unwanted;");
						break;
					case 34:
						lsBvirusName.append("Policy:Encrypted archive unwanted;");
						break;
					case 33:
						lsBvirusName.append("Policy:Archive recursion level exceeded;");
						break;
					case 32:
						lsBvirusName.append("Policy:Mailbomb unwanted;");
						break;
					case 31:
						lsBvirusName.append("Policy:Corrupted archive unwanted;");
						break;
					case 30:
						lsBvirusName.append("Policy:Multipart archive unwanted;");
						break;
					case 23:
						lsBvirusName.append("Policy:Generic Body Filter;");
						break;
					case 22:
						lsBvirusName.append("Policy:Media type blacklisted;");
						break;
					case 21:
						lsBvirusName.append("Policy:Media type mismatch;");
						break;
					default:
						lsStatus = "Infected:";
						break;
				}
				continue;
			}
			if (loLines[liCount].startsWith("ICAP/1.0 "))
			{
				lsStatus = "Error:" + loLines[liCount];
			}
		}
		if (lsBvirusName.toString().endsWith(";"))
		{
			lsBvirusName = new StringBuffer(lsBvirusName.substring(0, lsBvirusName.length() - 1));
		}
		if ("".equals(lsBvirusName.toString()))
		{
			lsBvirusName = new StringBuffer("Unknown");
		}
		lsReturnString = lsStatus + lsBlockResult + ":\"" + lsBvirusName.toString() + "\"";
		return lsReturnString;
	}
}
