package koh.cypher;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Cipher extends CipherAdapter {
	
	private MessageDigest md;
	
	public Md5Cipher() throws NoSuchAlgorithmException {
		md = MessageDigest.getInstance("MD5");
	}

	public byte[] cipher(byte[] bytes) {
		return md.digest(bytes);
	}

	@Override
	public String cipher(String string) {
		return cipherString(string.getBytes());
	}

	@Override
	public String cipherString(byte[] bytes) {
		BigInteger i = new BigInteger(1, cipher(bytes));
		return String.format("%1$032X", i);
	}
	
	

}
