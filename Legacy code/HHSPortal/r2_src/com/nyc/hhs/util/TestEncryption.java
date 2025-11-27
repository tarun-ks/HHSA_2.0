/**
 * 
 */
package com.nyc.hhs.util;

import com.nyc.hhs.exception.ApplicationException;

/**
 * @author a.rohilla
 *
 */
public class TestEncryption
{

	/**
	 * 
	 */
	public TestEncryption()
	{
	}

	/**
	 * @param args
	 * @throws ApplicationException 
	 */
	public static void main(String[] args) throws ApplicationException
	{
		HHSUtil loabc = new HHSUtil();
System.out.println(DecryptionUtil.decrytText("jlytd11jIqOuXcUbql4rSW6PUWH8kd6xrW2tsRUrl3s="));
	}

}
