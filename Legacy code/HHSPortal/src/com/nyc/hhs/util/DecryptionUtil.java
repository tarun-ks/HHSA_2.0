package com.nyc.hhs.util;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;

public class DecryptionUtil {

	/**
	 * @param args
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(DecryptionUtil.class);
	
	public static SecretKeySpec generateSecretKey() throws NoSuchAlgorithmException, UnsupportedEncodingException, UnknownHostException, ApplicationException 
	{
		
		String lsEnvironment = System.getProperty("hhs.env");
		String[] lsEnvArray = null;
		if(null != lsEnvironment && !lsEnvironment.isEmpty())
		{
			lsEnvArray = lsEnvironment.split("_");
		}
		else
		{
			lsEnvArray = new String[1];
			lsEnvArray[0] = "local";
		}
		byte[] loKey = (lsEnvArray[0]).getBytes("UTF-8");
		MessageDigest loSha = MessageDigest.getInstance("SHA-1");
		loKey = loSha.digest(loKey);
		loKey = Arrays.copyOf(loKey, 16); // use only first 128 bit
		SecretKeySpec loSkeySpec = new SecretKeySpec(loKey, "AES");
		return loSkeySpec;
	}

	public static Cipher createCipher() throws NoSuchAlgorithmException,NoSuchPaddingException 
	{
		Cipher loDesCipher;
		loDesCipher = Cipher.getInstance("AES");
		return loDesCipher;
	}

	public static Cipher initializeEncrytCipher(Cipher aoCipher, SecretKeySpec aoKey) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException 
	{
		aoCipher.init(Cipher.ENCRYPT_MODE, aoKey);
		return aoCipher;
	}
	
	public static Cipher initializeDecrytCipher(Cipher aoCipher, SecretKeySpec aoKey) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException 
	{
		aoCipher.init(Cipher.DECRYPT_MODE, aoKey);
		return aoCipher;
	}
	
	public static String decrytText(String asTextToDecrypt)throws ApplicationException
	{
		LOG_OBJECT.Debug("Inside 'decrytByteText' method.");
		String lsDecrytedText = null;
		try 
		{
			SecretKeySpec loKey = generateSecretKey();
			Cipher loCipher = createCipher();
			lsDecrytedText = new String(initializeDecrytCipher(loCipher, loKey).doFinal(Base64.decodeBase64(asTextToDecrypt)));
			LOG_OBJECT.Debug("String Decrytion Successful.");
		}
		catch (Exception aoEx) 
		{
			LOG_OBJECT.Error("Error while Decryting text in decrytByteText: ", aoEx);
		}
		return lsDecrytedText;
	}
}
