package com.alexdiru.redleaf;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import android.provider.MediaStore.Files;

public abstract class UtilsCrypto {
	
	private static final String mKey = "hisfuGD783GYUWIA NWAT (AWT^T£][";
	
	/**
	 * Sets up the encryptor/decryptor
	 * @param key The key to use to encrypt/decrypt
	 * @throws Exception
	 */
	/*public HelperCrypto(String key) throws Exception {
	}*/

	/**
	 * Encrypts a file
	 * @param inFile The file to encrypt
	 * @param outFile Where the encrypted file will be created
	 * @throws Exception
	 */
	/*public void encryptFile(String inFile, String outFile) throws Exception {
		//Get byte stream of inFile
		byte[] inputBytes = HelperFileIO.getByteStream(filePath)
		
		//Encrypt the bytes
		byte[] encryptedBytes = encryptBytesAES(inputBytes);//CaesarCipher(inputBytes);
		
		//Write encrypted bytes to output file
		Files.write(Paths.get(outFile), encryptedBytes);
	}*/
	
	/**
	 * Decrypt a file
	 * @param inFile The file to decrypt
	 * @param outFile Where the decrypted file will be created
	 * @throws Exception
	 */
	public static byte[] decryptFile(String inFile) throws Exception {
		//Get the raw key
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(mKey.getBytes());
        kgen.init(128, sr); //128 bit encryption
		byte[] keyBytes = kgen.generateKey().getEncoded();
		
		//Get byte stream of inFile
		byte[] inputBytes = UtilsFileIO.getByteStream(inFile);
		
		//Decrypt the bytes
		return decryptBytesAES(inputBytes, keyBytes);
	}
	
	/*private byte[] encryptBytesAES(byte[] bytes) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(mKeyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
    	byte[] encrypted = cipher.doFinal(bytes);
    	return encrypted;
    }*/
	
	private static byte[] decryptBytesAES(byte[] bytes, byte[] keyBytes) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        try {
        	byte[] decrypted = cipher.doFinal(bytes);
        	return decrypted;
        } catch (Exception ex ){
        	return null;
        }
	}

}
