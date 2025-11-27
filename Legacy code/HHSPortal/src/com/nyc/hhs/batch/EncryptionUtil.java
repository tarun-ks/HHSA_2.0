package com.nyc.hhs.batch;

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

/**
 * 
 * This EncryptionUtil is for generating encrypted text for user-id and password
 * 
 */

public class EncryptionUtil
{

	private static final LogInfo LOG_OBJECT = new LogInfo(EncryptionUtil.class);

	/**
	 * This Main method is the execution point of the batch processes
	 * 
	 * @param args required while running this from script/command prompt
	 */
	public static void main(String[] args) throws ApplicationException
	{

		LOG_OBJECT.Info("Obtained args[0] : " + args[0]);
		LOG_OBJECT.Info("Obtained args[1] : " + args[1]);

		String lsTextToEncrypt = args[0];
		String lsHostName = args[1];

		try
		{
			LOG_OBJECT.Info("Text to Encrypt: " + lsTextToEncrypt);
			byte[] lbTextEncrypted = encrytText(lsTextToEncrypt, lsHostName);
			LOG_OBJECT.Info("Encryted Text: " + new String(lbTextEncrypted, "ISO-8859-1"));

		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while Encryting/Decryting text in EncryptUtil", aoEx);
		}
	}

	/**
	 * This method is used to generate Secret Key
	 * @param String asHostName
	 * @throws NoSuchAlgorithmException, UnsupportedEncodingException, UnknownHostException If an Application Exception occurs
	 * @return loSkeySpec - SecretKeySpec
	 */

	public static SecretKeySpec generateSecretKey(String asHostName) throws NoSuchAlgorithmException,
			UnsupportedEncodingException, UnknownHostException
	{
		byte[] loKey = (asHostName).getBytes("UTF-8");
		MessageDigest loSha = MessageDigest.getInstance("SHA-1");
		loKey = loSha.digest(loKey);
		loKey = Arrays.copyOf(loKey, 16); // use only first 128 bit
		SecretKeySpec loSkeySpec = new SecretKeySpec(loKey, "AES");
		return loSkeySpec;
	}
	/**
	 * This method is used to create Cipher
	 * @throws NoSuchPaddingException, NoSuchAlgorithmException If an Application Exception occurs
	 * @return loDesCipher - Cipher.
	 */
	public static Cipher createCipher() throws NoSuchAlgorithmException, NoSuchPaddingException
	{
		Cipher loDesCipher;
		loDesCipher = Cipher.getInstance("AES");
		return loDesCipher;
	}
	/**
	 * This method is used to create Cipher
	 * @param Cipher aoCipher
	 * @param SecretKeySpec aoKey
	 * @throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException If an Application Exception occurs
	 * @return aoCipher - Cipher.
	 */
	public static Cipher initializeEncrytCipher(Cipher aoCipher, SecretKeySpec aoKey) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException
	{
		aoCipher.init(Cipher.ENCRYPT_MODE, aoKey);
		return aoCipher;
	}
	/** 
	 * This method is used to initialize Decrypt Cipher
	 * @param Cipher aoCipher
	 * @param SecretKeySpec aoKey
	 * @throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException If an Application Exception occurs
	 * @return aoCipher - Cipher.
	 */
	public static Cipher initializeDecrytCipher(Cipher aoCipher, SecretKeySpec aoKey) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException
	{
		aoCipher.init(Cipher.DECRYPT_MODE, aoKey);
		return aoCipher;
	}
	/** 
	 * This method is used to initialize Decrypt Cipher
	 * @param Cipher aoCipher
	 * @param SecretKeySpec aoKey
	 * @throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException If an Application Exception occurs
	 * @return aoCipher - Cipher.
	 */
	public static byte[] encrytText(String asTextToEncrypt, String asHostName) throws ApplicationException
	{
		LOG_OBJECT.Debug("Inside 'encrytStringText' method.");
		byte[] lbEncrytedString = null;
		try
		{
			SecretKeySpec loKey = generateSecretKey(asHostName);
			Cipher loCipher = createCipher();
			lbEncrytedString = Base64.encodeBase64(initializeEncrytCipher(loCipher, loKey).doFinal(
					asTextToEncrypt.getBytes()));
			LOG_OBJECT.Debug("String Encrytion Successful.");
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while Encryting text in decrytStringText: ", aoEx);
		}
		return lbEncrytedString;
	}
	/** 
	 * This method is used to decryt Text
	 * @param String asTextToDecrypt
	 * @param String asHostName
	 * @throws ApplicationException If an Application Exception occurs
	 * @return lsDecrytedText - String.
	 */
	public static String decrytText(String asTextToDecrypt, String asHostName) throws ApplicationException
	{
		LOG_OBJECT.Debug("Inside 'decrytByteText' method.");
		String lsDecrytedText = null;
		try
		{
			SecretKeySpec loKey = generateSecretKey(asHostName);
			Cipher loCipher = createCipher();
			lsDecrytedText = new String(initializeDecrytCipher(loCipher, loKey).doFinal(
					Base64.decodeBase64(asTextToDecrypt)));
			LOG_OBJECT.Debug("String Decrytion Successful.");
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while Decryting text in decrytByteText: ", aoEx);
		}
		return lsDecrytedText;
	}

}
